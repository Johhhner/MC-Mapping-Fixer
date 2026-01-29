package net.mappingfixer.fmf;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.lang.String;
import java.util.regex.Matcher;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

class MData {
	ObjectPointer<String> linePointer;
	String simpleMethodName;
	String fullMethodName;
	String methodDesc;

	public MData(ObjectPointer<String> mappingLineEntry) throws Exception {
		linePointer = mappingLineEntry;

		Matcher matcher = PatternHolder.mapping.matcher(mappingLineEntry.get());
		if (!matcher.find()) {
			throw new Exception("Unable to find method data in mappings!\n");
		}

		matcher.find();
		methodDesc = matcher.group();

		matcher.find();
		simpleMethodName = matcher.group();

		fullMethodName = simpleMethodName + methodDesc;
	}
};

class CData {
	public final String className;

	public HashMap<String, MData> methods;

	public CData(ListIterator<String> start, int end) throws Exception {
		methods = new HashMap<String, MData>();
		
		Matcher matcher = PatternHolder.classNameHolder.matcher(start.get());
		if (matcher.find()) {
			className = matcher.group();
		} else {
			throw new Exception("Unable to find class name in: " + start.get() + "!\n");
		}

		// Skip the current position, as we just used it.
		start.inc();

		start.iterate(end, (list, index) -> {
			String str = list.get(index);
			Matcher matcher2 = PatternHolder.srgMethMatcher.matcher(str);

			if (matcher2.find()) {
				MData mdat = new MData(new ObjectPointer<String>() {
					private final int i = index;

					@Override
					public String get() {
						return list.get(i);
					}

					@Override
					public void set(String value) {
						list.set(i, value);
					}

				});

				methods.put(mdat.fullMethodName, mdat);
			}
		});
	}
};

class JData {
	List<CData> classes;
	List<String> theEntireFuckingFile;
	String srgMcpMapPath;

	@SuppressWarnings("resource")
	public JData(String srgToMCPPath) throws Exception {
		this.classes = new ArrayList<CData>();
		this.theEntireFuckingFile = new ArrayList<String>();
		
		this.srgMcpMapPath = srgToMCPPath;

		File srgToMCPFile = new File(srgToMCPPath);

		Scanner myReader = new Scanner(srgToMCPFile);
		while (myReader.hasNextLine()) {
			theEntireFuckingFile.add(myReader.nextLine());
		}

		theEntireFuckingFile.add("ending"); // This is the easiest way to get the proceeding code to function correctly.

		int end = theEntireFuckingFile.size() - 1;

		ListIterator<String> iter = new ListIterator<String>(theEntireFuckingFile, 0, end).inc();
		iter.iterate(end, (list, index) -> {
			Matcher matcher = PatternHolder.lineCharStart.matcher(list.get(index));

			if (matcher.find()) {
				CData cdat = new CData(iter, iter.find_if(index + 1, end, (String str) -> {
					Matcher matcher2 = PatternHolder.lineCharStart.matcher(str);

					return matcher2.find();
				}) - 1);

				classes.add(cdat);
				iter.dec();
			}
		});
	}

	void dumpOutput(String location) throws IOException {
		File file = new File(location);
		file.delete();

		FileWriter output = new FileWriter(location);
		for (String str : theEntireFuckingFile) {
			output.write(str + "\n");
		}

		output.close();
	}
};