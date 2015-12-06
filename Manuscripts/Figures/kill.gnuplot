#!/usr/bin/gnuplot -p
    # set border 3
    set bmargin 3 
    set tmargin 0.4
    set rmargin 0.2
    set lmargin 6
    set format y "2^{%L}"
    set datafile separator ","
    set logscale y 2
    set logscale x 2
    set ylabel "Time (sec)"
    set xlabel "chain length" 
    set xrange [1:35]
    plot "../Figures/kill.csv" every ::1 using 1:2 notitle with linespoints
