package net.mappingfixer.fmf;

import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

final class PatternHolder {
	public static final Pattern lineCharStart = Pattern.compile("^\\w");

	// the casual 40 char regex..
	// public static final Pattern classNameHolder = Pattern.compile("^((\\w)+[/|$])+(\\w)+(?= ((\\w)+[/|$])+(\\w)+$)");
	public static final Pattern classNameHolder = Pattern.compile("^\\w.*(?= )");

	public static final Pattern srgMethMatcher = Pattern.compile("m_\\w*_");

	public static final Pattern mapping = Pattern.compile("[^ \\t\\n]+");
	
	public static final Pattern fileExtensionMatcher = Pattern.compile("(?!.*\\.).*$");
	
	// TODO: This is actually useless. Why am I using it instead of startwith?
	public static final Pattern forLambda = Pattern.compile("^lambda");
	
	public static final Pattern partsOfMapping = Pattern.compile("([^ \\t\\n]+)$");
}

interface Lambda<T, A> {
	T run(A value);
}

interface ObjectPointer<T> {
	public T get();
	public void set(T value);
}

class ListIterator<T> {
	private final int min;
	private final int max;
	private Integer index;
	private final List<T> LIST;

	interface TestLambda<T> {
		boolean run(T value);
	}

	interface IterationLambda<T> {
		void run(List<T> list, Integer index) throws Exception;
	}

	public ListIterator(List<T> list, int min, int max) {
		this.min = min;
		this.max = max;
		this.index = this.min;
		LIST = list;
	}
	
	public ListIterator(List<T> list) {
		this.min = 0;
		this.max = list.size() - 1;
		this.index = this.min;
		LIST = list;
	}

	public int find_if(int from, int to, TestLambda<T> test) {
		for (int i = from; i <= to; ++i) {
			if (test.run(LIST.get(i))) {
				return i;
			}
		}

		return -1;
	}

	public void iterate(int to, IterationLambda<T> iteratormeth) throws Exception {
		for (; index <= to && hasNext(); ++index) {
			iteratormeth.run(LIST, index);
		}
	}

	public boolean hasNext() {
		return index < max;
	}

	public T get() {
		return LIST.get(index);
	}

	public ListIterator<T> inc() {
		++index;
		return this;
	}

	public ListIterator<T> dec() {
		--index;
		return this;
	}
	
	public int getMax() {
		return this.max;
	}
	
	public void reset() {
		this.index = this.min;
	}

	public ListIterator<T> add(int amount) {
		index += amount;
		return this;
	}
}

class Utilities {
	
	public static int FastAbs(int value) {
		if (value < 0)
			return -value;
		return value;
	}
	
	// Jus' make sure y'ain't puttin' any dots in ya' folders.
	// Redundant and easily done much more efficiently using endsWith.
	public static String getFileExtension(String fileName) {
		Matcher matcher = PatternHolder.fileExtensionMatcher.matcher(fileName);
		
		if(matcher.find()) {
			return matcher.group();
		}
		
		return "";
	}
}