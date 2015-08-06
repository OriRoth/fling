package org.spartan.fajita.api.examples.old;

import java.util.ArrayList;
import java.util.List;

import org.spartan.fajita.api.ast.Atomic;
import org.spartan.fajita.api.ast.Compound;
import org.spartan.fajita.api.bnf.BNF;
import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.Terminal;
/**
 * See note 5 on {@link org.spartan.fajita.api.Thoughts} about bottom-up parsing
 * 
 * @author Tomer
 *
 */
public class ConcurrencyBuilderExample {

	public class Concurrently_With_Opt extends Compound {

		public Concurrently_With_Opt(final Compound parent) {
			super(parent, "CONC_OPT");
		}

		@Override
		public List<Compound> getChildren() {
			return null;
		}
		
	}
	
	public class ConcurrentlyWith extends Compound {

		private final Runnable j;

		public ConcurrentlyWith(final Compound parent, final Runnable j) {
			super(parent,"CONC");
			this.j = j;
		}

		public ConcurrentlyWith concurrentlyWith(final Runnable job) {
			ConcurrentlyWith son = new ConcurrentlyWith(this, job);
			
			return son;
		}

		@Override
		public List<Compound> getChildren() {
			List<Compound> $ = new ArrayList<>();
			$.add(new Atomic(this,"concurrentlyWith",j));
			$.add(new )
			return $;
		}
	}

	public class Timeout extends Compound {

		private final int milliseconds;

		public Timeout(final Compound parent, final int milliseconds) {
			super(parent,"TIMEOUT");
			this.milliseconds = milliseconds;
		}

		public ConcurrentlyWith concurrentlyWith(final Runnable j) {
			return new ConcurrentlyWith(getParent(), j);
		}

		@Override
		public List<Compound> getChildren() {
			List<Compound> $ = new ArrayList<>();
			$.add(new Atomic(this,"timeout",milliseconds));
			return $;
		}
	}

	public class On extends Atomic {

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

	public class RunJobs extends Atomic {

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
	public class ConcurrencyBuilder extends Compound {
		public RunJobs runJobs(final Runnable... runnables) {
			return new RunJobs(this, runnables);
		}
		
	}

	public static void main(final String[] args) {
		BNF b = new BNF();
		
		// defining NonTerminals
		final NonTerminal S = b.addNT("S")
				, RunJobs = b.addNT("RUN_JOBS")
				, Timeout_Opt = b.addNT("TIMEOUT_OPT")
				, Timeout = b.addNT("TIMEOUT")
				, Concurrently_Opt = b.addNT("CONC_OPT")
				, Concurrently = b.addNT("CONC");

		// defining Terminals
		final Terminal run_jobs = b.addTerm("run_jobs")
				,on = b.addTerm("on")
				,concurrentlyWith = b.addTerm("concurrentlyWith")
				,timeout = b.addTerm("timeout");
		
		// Adding rules
		b		.derive(S).toOneOf(RunJobs)
				.derive(RunJobs).to(run_jobs,on,Timeout_Opt,Concurrently_Opt)
				.derive(Timeout_Opt).toOneOf(Timeout,b.EPSILON)
				.derive(Timeout).to(timeout)
				.derive(Concurrently_Opt).toOneOf(Concurrently,b.EPSILON)
				.derive(Concurrently).to(concurrentlyWith,Concurrently_Opt);
		
		System.out.println(b);
		Runnable job = () -> nop();

		ConcurrencyBuilder cb = new ConcurrencyBuilder();

		cb.runJobs(job, job, job).on(3).concurrentlyWith(job);

		cb.runJobs(job, job, job, job, job, job, job).on(7).timeout(500).concurrentlyWith(job).concurrentlyWith(job);
		
	}

	public static void nop() {
	};
}
