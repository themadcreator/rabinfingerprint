package org.rabinfingerprint;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.rabinfingerprint.Args.ArgsModel.Mode;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class Args {
	@SuppressWarnings("serial")
	public static class ArgParseException extends Exception{
		public ArgParseException(String arg0, Throwable arg1) {
			super(arg0, arg1);
		}

		public ArgParseException(String arg0) {
			super(arg0);
		}
	}

	public static abstract class Arg {
		private final Set<String> flags;
		private final int argValueCount;
		
		public Arg(int argValueCount, String... flags) {
			this.argValueCount = argValueCount;
			this.flags = Sets.newHashSet(flags);
		}

		public boolean isFlagOf(String arg) {
			return flags.contains(arg);
		}

		public int getArgValueCount() {
			return argValueCount;
		}
		
		public abstract void parse(ArgsModel model, String[] strs) throws ArgParseException;
	}
	
	public static class ArgsModel{
		public static enum Mode{
			HELP, POLYGEN, FINGERPRINT_FILES, FINGERPRINT_STDIN;
		}
		
		public Mode mode = Mode.HELP;
		public int degree = 53;
		public long polynomial = 0;
		public List<String> unflagged = Lists.newArrayList();
	}
	
	List<Arg> args = Lists.newArrayList();
	
	public Args() {
		args.add(new Arg(0, "-h", "--help") {
			@Override
			public void parse(ArgsModel model, String[] strs) {
				model.mode = Mode.HELP;
			}
		});
		args.add(new Arg(1, "-polygen") {
			@Override
			public void parse(ArgsModel model, String[] strs) throws ArgParseException {
				model.mode = Mode.POLYGEN;
				try {
					model.degree = Integer.parseInt(strs[0]);
				} catch (NumberFormatException e) {
					throw new ArgParseException("Could not parse polynomial degree.");
				}
			}
		});
		args.add(new Arg(1, "-p") {
			@Override
			public void parse(ArgsModel model, String[] strs) throws ArgParseException {
				model.mode = Mode.FINGERPRINT_FILES;
				try {
					model.polynomial = Long.parseLong(strs[0], 16);
				} catch (NumberFormatException e) {
					throw new ArgParseException("Could not parse polynomial.");
				}
			}
		});
	}
	
	public ArgsModel parse(String[] strs) throws ArgParseException {
		ArgsModel model = new ArgsModel();
		
		for(int i = 0; i < strs.length;){
			String str = strs[i];
			boolean flagged = false;
			for (Arg arg : args) {
				if (arg.isFlagOf(str)) {
					arg.parse(model, Arrays.copyOfRange(strs, i + 1, i + 1 + arg.getArgValueCount()));
					i += 1 + arg.getArgValueCount();
					flagged = true;
					break;
				}
			}
			if(!flagged){
				model.unflagged.addAll(Lists.newArrayList(Arrays.copyOfRange(strs, i, strs.length)));
				break;
			}
			
		}
		
		if(model.mode == Mode.FINGERPRINT_FILES && model.unflagged.size() == 0){
			model.mode = Mode.FINGERPRINT_STDIN;
		}
		
		return model;
	}
}
