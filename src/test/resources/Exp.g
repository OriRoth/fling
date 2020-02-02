grammar Exp;

eval
    :    additionExp
    ;

additionExp
    :    multiplyExp 
         (plusMinusExp)*
    ;

plusMinusExp
    :    'plus' multiplyExp 
         | 'minus' multiplyExp
    ;

multiplyExp
    :    atomExp
         (multDivExp)*
    ;

multDivExp
    :    'mult' atomExp 
         | 'div' atomExp
    ;

atomExp
    :    'i:type:int'
    |    'p' additionExp 'q'
    ;
