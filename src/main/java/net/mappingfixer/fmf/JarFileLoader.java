package net.mappingfixer.fmf;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.nio.file.Files;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.ArrayList;
import java.util.Enumeration;

class AsmCodeUnit {
	public int codeID;
	public String codeDesc;

	public AsmCodeUnit(AbstractInsnNode code) {
		codeID = code.getOpcode();

		if (code.getType() == AbstractInsnNode.FIELD_INSN) {
			codeDesc = ((FieldInsnNode) code).name;
		} else {
			codeDesc = "";
		}
	}

	boolean equals(AsmCodeUnit other, int prec) {
		return Utilities.FastAbs(codeID - other.codeID) <= prec && codeDesc.contentEquals(other.codeDesc);
	}
};

class MUnit {
	public boolean isLambda;

	public String simpleMethodName;
	public String fullMethodName;
	public String methodDesc;
	public List<AsmCodeUnit> methodCodes;

	private void populateCodes(MethodNode meth) {
		for (AbstractInsnNode code : meth.instructions.toArray()) {
			if(code.getOpcode() != -1)
				methodCodes.add(new AsmCodeUnit(code));
		}
	}

	public MUnit(MethodNode meth) {
		methodCodes = new ArrayList<AsmCodeUnit>();

		simpleMethodName = meth.name;
		//System.out.println(String.format("The simple name of the method is %s", simpleMethodName));

		Matcher matcher = PatternHolder.forLambda.matcher(simpleMethodName);
		if (!matcher.find()) {
			isLambda = false;
			return;
		}

		isLambda = true;

		methodDesc = meth.desc;
		//System.out.println(String.format("The descriptor of the method is %s", methodDesc));

		fullMethodName = simpleMethodName + methodDesc;

		populateCodes(meth);
	}

	private boolean checkEquals(MUnit other, int precision) {
		if (methodCodes.size() != other.methodCodes.size()) {
			return false;
		}

		boolean result = methodDesc.contentEquals(other.methodDesc);

		for (int i = 0; i < methodCodes.size(); i++) {
			result &= methodCodes.get(i).equals(other.methodCodes.get(i));
		}

		return result;
	}
	
	boolean equalsVeryUnsafe(MUnit other) {
		return methodDesc.contentEquals(other.methodDesc)
				&& methodCodes.size() == other.methodCodes.size();
	}
	
	boolean equalsExtremelyUnsafe(MUnit other) {
		return methodDesc.contentEquals(other.methodDesc);
	}
	
	boolean equalsUnsafe(MUnit other, int precision) {
		return this.checkEquals(other, precision);
	}
	
	boolean equals(MUnit other) {
		return this.checkEquals(other, 0);
	}
};

class CUnit {
	String className;
	List<MUnit> methods;

	public CUnit(ClassNode classdat) {
		methods = new ArrayList<MUnit>();

		className = classdat.name;

		for (Object obj : classdat.methods) {
			MUnit meth = new MUnit((MethodNode) obj);

			if (meth.isLambda) {
				methods.add(meth);
			}
		}
	}
};

class JUnit {
	public HashMap<String, CUnit> classes;

	private void makeClassNode(final byte[] bytes) {
		ClassNode node = new ClassNode();
		ClassReader cr = new ClassReader(bytes);
		cr.accept(node, 0);

		CUnit cunit = new CUnit(node);
		classes.put(node.name, cunit);
	}

	@SuppressWarnings("resource")
	private void generateEveryClassFromJar(String path) throws IOException {
		ZipFile zip = new ZipFile(path);

		Enumeration<? extends ZipEntry> entries = zip.entries();
		while (entries.hasMoreElements()) {
			ZipEntry entry = entries.nextElement();

			if (entry.getName().endsWith(".class")) {
				InputStream stream = zip.getInputStream(entry);
				makeClassNode(stream.readAllBytes());
			}
		}
	}

	private void generateEveryClassFromPath(File path) throws IOException {
		for (File file : path.listFiles()) {
			if (file.isDirectory()) {
				generateEveryClassFromPath(file);
			} else if (file.getAbsolutePath().endsWith(".class")) {
				makeClassNode(Files.readAllBytes(file.toPath()));
			}
		}
	}

	public JUnit(String jarClassFilesPath) throws IOException {
		classes = new HashMap<String, CUnit>();

		if (jarClassFilesPath.endsWith(".jar")) {
			generateEveryClassFromJar(jarClassFilesPath);
		} else {
			File jarClassFile = new File(jarClassFilesPath);

			generateEveryClassFromPath(jarClassFile);
		}
	}
};