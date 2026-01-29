package net.mappingfixer.fmf;

import java.lang.String;
import java.util.regex.Matcher;

public class MainSource {
	public static void combineJDataJUnit(JData jdata, JUnit from, JUnit to) {
		for (CData currClass : jdata.classes) {
			if (!from.classes.containsKey(currClass.className))
				continue;
			
			for (MUnit currMeth : from.classes.get(currClass.className).methods) {
				if(!currClass.methods.containsKey(currMeth.fullMethodName)) {
					continue;
				}
				if (!to.classes.containsKey(currClass.className)) {
					continue;
				}

				// Was some dirty *ss programming
				// Not *that* dirty anymore (still ain't good though)
				ListIterator<MUnit> methods = new ListIterator<MUnit>(to.classes.get(currClass.className).methods);
				int ret = methods.find_if(0, methods.getMax(),
				value -> {
					if(currMeth.equals(value))
						return true;
					else
						methods.inc();
					
					return false;
				});
				
				// TODO: Make this actually not look f*cking terrible.
				if(ret == -1) {
					// Try a very unsafe match if we don't find our lambda
					methods.reset();
					ret = methods.find_if(0, methods.getMax(),
					value -> {
						if(currMeth.equalsVeryUnsafe(value))
							return true;
						else
							methods.inc();
						
						return false;
					});
					
					if (ret == -1) {
						// Last chance to find *something* that might match.
						methods.reset();
						ret = methods.find_if(0, methods.getMax(),
						value -> {
							if(currMeth.equalsExtremelyUnsafe(value))
								return true;
							else
								methods.inc();
							
							return false;
						});
					}
				}
				
				ObjectPointer<String> linePtr = currClass.methods.get(currMeth.fullMethodName).linePointer;
				String tempStr = linePtr.get();
				Matcher matcher = PatternHolder.partsOfMapping.matcher(tempStr);
				
				if (ret == -1) {
					// Whelp! Tried everything, but the lambda simply doesn't exist.
					linePtr.set(matcher.replaceFirst(currMeth.simpleMethodName.replace("$", "\\$")));
					continue;
				}

				MUnit validMethod = methods.get();
				linePtr.set(matcher.replaceFirst(validMethod.simpleMethodName.replace("$", "\\$")));
			}
		}

		// TODO: MOVE TO DUMP OUTPUT
		jdata.theEntireFuckingFile.removeLast(); // Remove the "ending" helper
	}

	public static void main(String[] args) throws Exception {
		// TODO: Actually write proper English here.
		int argc = args.length;
		if (argc > 3) {
			throw new Exception("Bruddah, ya' got too many arguments here. "
					+ "Did ya' use quotes correctly?");
		}

		if (argc < 3) {
			throw new Exception("Bruddah, ya' don't got enough arguments here. "
					+ "Did ya' pass arguments for all of the followin':\n"
					+ "{Path to Forge Classes}, {Mappings Path}, "
					+ "and {Path to Recompiled Minecraft Jar}?");
		}

		JUnit junitforge = new JUnit(args[0]);
		JUnit junitmcp = new JUnit(args[2]);

		JData jdata = new JData(args[1]);

		combineJDataJUnit(jdata, junitmcp, junitforge);

		jdata.dumpOutput(args[1]);
	}
}