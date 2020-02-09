grammar TableMaker;

eval
    :    table
    ;

table
    :    'cell'
    |    'row' (table)+ 'seal'
    |    'column' (table)+ 'seal'
    ;
