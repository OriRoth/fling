case0.diff:

%.output: %.input
	cpp $< $@

%.diff: %.expected %.output
	diff $^ > $@
