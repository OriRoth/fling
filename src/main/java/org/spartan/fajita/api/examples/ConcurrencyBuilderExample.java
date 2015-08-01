package org.spartan.fajita.api.examples;

import static org.spartan.fajita.api.bnf.BNF.optional;

import java.util.ArrayList;
import java.util.List;

import org.spartan.fajita.api.bnf.BNF;
/**
 * See note 5 on {@link org.spartan.fajita.api.Thoughts} about bottom-up parsing
 * 
 * @author Tomer
 *
 */
public class ConcurrencyBuilderExample {

	/**
	 * has an ordered list of children
	 * 
	 * @author Tomer
	 *
	 */
	public static class Compound {

		public final List<Compound> children;

		public Compound(final Compound... nodes) {
			children = new ArrayList<>();
			for (Compound compound : nodes)
				children.add(compound);
		}
	}

	public static class Atomic extends Compound {

		public final Object content;

		public Atomic(final Object content) {
			super(new Compound[] {});
			this.content = content;
		}
	}

	public static class ConcurrentlyWith extends Atomic {

		public ConcurrentlyWith(final Compound invoker, final Runnable j) {
			super(j);
		}

		public ConcurrentlyWith concurrentlyWith(final Runnable job) {
			return new ConcurrentlyWith(this, job);
		}
	}

	public static class Timeout extends Atomic {

		public Timeout(final Compound invoker, final int milliseconds) {
			super(milliseconds);
		}

		public ConcurrentlyWith concurrentlyWith(final Runnable j) {
			return new ConcurrentlyWith(this, j);
		}
	}

	public static class On extends Atomic {

		public On(final Compound invoker, final int threadsNumber) {
			super(threadsNumber);
		}

		public Timeout timeout(final int milliseconds) {
			return new Timeout(this, milliseconds);
		}

		public ConcurrentlyWith concurrentlyWith(final Runnable j) {
			return new ConcurrentlyWith(this, j);
		}
	}

	public static class RunJobs extends Atomic {

		public RunJobs(final Compound invoker, final Runnable... runnables) {
			super(runnables);
		}

		public On on(final int threadsNumber) {
			return new On(this, threadsNumber);
		}
	}

	/**
	 * This serves in some way the S nonterminal
	 * 
	 * @author Tomer
	 *
	 */
	public static class ConcurrencyBuilder extends Compound {
		public RunJobs runJobs(final Runnable... runnables) {
			return new RunJobs(this, runnables);
		}
	}

	public static void main(final String[] args) {
		BNF concurrencyBnf = new BNF();
		concurrencyBnf.nonterminal("S").isOneOf("RUN_JOBS")
				.nonterminal("RUN_JOBS").derivesTo("run_jobs","on",optional("TIMEOUT"),optional("CONC"))
				.nonterminal("TIMEOUT").derivesTo("timeout")
				.nonterminal("CONC").derivesTo("concurrentlyWith",optional("CONC"));
		System.out.println(concurrencyBnf.toString());

		Runnable job = () -> nop();

		ConcurrencyBuilder cb = new ConcurrencyBuilder();

		cb.runJobs(job, job, job).on(3).concurrentlyWith(job);

		cb.runJobs(job, job, job, job, job, job, job).on(7).timeout(500).concurrentlyWith(job).concurrentlyWith(job);
	}

	public static void nop() {
	};
}
