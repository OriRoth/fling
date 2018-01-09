package org.spartan.fajita.revision.examples;

import static org.spartan.fajita.revision.api.Fajita.option;
import static org.spartan.fajita.revision.examples.Exp2.NT.*;
import static org.spartan.fajita.revision.examples.Exp2.Term.*;

import java.io.IOException;

import org.spartan.fajita.revision.api.Fajita;
import org.spartan.fajita.revision.api.Fajita.FajitaBNF;
import org.spartan.fajita.revision.api.Main;
import org.spartan.fajita.revision.export.Grammar;
import org.spartan.fajita.revision.symbols.NonTerminal;
import org.spartan.fajita.revision.symbols.Terminal;

public class Exp2 extends Grammar {
  public static enum Term implements Terminal {
    a
  }

  public static enum NT implements NonTerminal {
    S, //
    A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19, A20, A21, A22, A23, A24, A25, A26, A27, A28, A29, A30, A31, A32, A33, A34, A35, A36, A37, A38, A39, A40, A41, A42, A43, A44, A45, A46, A47, A48, A49, A50, A51, A52, A53, A54, A55, A56, A57, A58, A59, A60, A61, A62, A63, A64, A65, A66, A67, A68, A69, A70, A71, A72, A73, A74, A75, A76, A77, A78, A79, A80, A81, A82, A83, A84, A85, A86, A87, A88, A89, A90, A91, A92, A93, A94, A95, A96, A97, A98, A99, A100, A101, A102, A103, A104, A105, A106, A107, A108, A109, A110, A111, A112, A113, A114, A115, A116, A117, A118, A119, A120, A121, A122, A123, A124, A125, A126, A127, A128, A129, A130, A131, A132, A133, A134, A135, A136, A137, A138, A139, A140, A141, A142, A143, A144, A145, A146, A147, A148, A149, A150, A151, A152, A153, A154, A155, A156, A157, A158, A159, A160, A161, A162, A163, A164, A165, A166, A167, A168, A169, A170, A171, A172, A173, A174, A175, A176, A177, A178, A179, A180, A181, A182, A183, A184, A185, A186, A187, A188, A189, A190, A191, A192, A193, A194, A195, A196, A197, A198, A199, A200, A201, A202, A203, A204, A205, A206, A207, A208, A209, A210, A211, A212, A213, A214, A215, A216, A217, A218, A219, A220, A221, A222, A223, A224, A225, A226, A227, A228, A229, A230, A231, A232, A233, A234, A235, A236, A237, A238, A239, A240, A241, A242, A243, A244, A245, A246, A247, A248, A249, A250, A251, A252, A253, A254
  }

  @Override public FajitaBNF bnf() {
    return Fajita.build(Exp2.class, Term.class, NT.class, "Exp2", Main.packagePath, Main.projectPath) //
        .start(S) //
        .derive(S).to(A0) //
        .derive(A0).to(a, option(A1), a, option(A2), a) //
        .derive(A1).to(a, option(A3), a, option(A4), a) //
        .derive(A3).to(a, option(A7), a, option(A8), a) //
        .derive(A7).to(a, option(A15), a, option(A16), a) //
        .derive(A15).to(a, option(A31), a, option(A32), a) //
        .derive(A31).to(a, option(A63), a, option(A64), a) //
        .derive(A63).to(a, option(A127), a, option(A128), a) //
        .derive(A127).to(a) //
        .derive(A128).to(a) //
        .derive(A64).to(a, option(A129), a, option(A130), a) //
        .derive(A129).to(a) //
        .derive(A130).to(a) //
        .derive(A32).to(a, option(A65), a, option(A66), a) //
        .derive(A65).to(a, option(A131), a, option(A132), a) //
        .derive(A131).to(a) //
        .derive(A132).to(a) //
        .derive(A66).to(a, option(A133), a, option(A134), a) //
        .derive(A133).to(a) //
        .derive(A134).to(a) //
        .derive(A16).to(a, option(A33), a, option(A34), a) //
        .derive(A33).to(a, option(A67), a, option(A68), a) //
        .derive(A67).to(a, option(A135), a, option(A136), a) //
        .derive(A135).to(a) //
        .derive(A136).to(a) //
        .derive(A68).to(a, option(A137), a, option(A138), a) //
        .derive(A137).to(a) //
        .derive(A138).to(a) //
        .derive(A34).to(a, option(A69), a, option(A70), a) //
        .derive(A69).to(a, option(A139), a, option(A140), a) //
        .derive(A139).to(a) //
        .derive(A140).to(a) //
        .derive(A70).to(a, option(A141), a, option(A142), a) //
        .derive(A141).to(a) //
        .derive(A142).to(a) //
        .derive(A8).to(a, option(A17), a, option(A18), a) //
        .derive(A17).to(a, option(A35), a, option(A36), a) //
        .derive(A35).to(a, option(A71), a, option(A72), a) //
        .derive(A71).to(a, option(A143), a, option(A144), a) //
        .derive(A143).to(a) //
        .derive(A144).to(a) //
        .derive(A72).to(a, option(A145), a, option(A146), a) //
        .derive(A145).to(a) //
        .derive(A146).to(a) //
        .derive(A36).to(a, option(A73), a, option(A74), a) //
        .derive(A73).to(a, option(A147), a, option(A148), a) //
        .derive(A147).to(a) //
        .derive(A148).to(a) //
        .derive(A74).to(a, option(A149), a, option(A150), a) //
        .derive(A149).to(a) //
        .derive(A150).to(a) //
        .derive(A18).to(a, option(A37), a, option(A38), a) //
        .derive(A37).to(a, option(A75), a, option(A76), a) //
        .derive(A75).to(a, option(A151), a, option(A152), a) //
        .derive(A151).to(a) //
        .derive(A152).to(a) //
        .derive(A76).to(a, option(A153), a, option(A154), a) //
        .derive(A153).to(a) //
        .derive(A154).to(a) //
        .derive(A38).to(a, option(A77), a, option(A78), a) //
        .derive(A77).to(a, option(A155), a, option(A156), a) //
        .derive(A155).to(a) //
        .derive(A156).to(a) //
        .derive(A78).to(a, option(A157), a, option(A158), a) //
        .derive(A157).to(a) //
        .derive(A158).to(a) //
        .derive(A4).to(a, option(A9), a, option(A10), a) //
        .derive(A9).to(a, option(A19), a, option(A20), a) //
        .derive(A19).to(a, option(A39), a, option(A40), a) //
        .derive(A39).to(a, option(A79), a, option(A80), a) //
        .derive(A79).to(a, option(A159), a, option(A160), a) //
        .derive(A159).to(a) //
        .derive(A160).to(a) //
        .derive(A80).to(a, option(A161), a, option(A162), a) //
        .derive(A161).to(a) //
        .derive(A162).to(a) //
        .derive(A40).to(a, option(A81), a, option(A82), a) //
        .derive(A81).to(a, option(A163), a, option(A164), a) //
        .derive(A163).to(a) //
        .derive(A164).to(a) //
        .derive(A82).to(a, option(A165), a, option(A166), a) //
        .derive(A165).to(a) //
        .derive(A166).to(a) //
        .derive(A20).to(a, option(A41), a, option(A42), a) //
        .derive(A41).to(a, option(A83), a, option(A84), a) //
        .derive(A83).to(a, option(A167), a, option(A168), a) //
        .derive(A167).to(a) //
        .derive(A168).to(a) //
        .derive(A84).to(a, option(A169), a, option(A170), a) //
        .derive(A169).to(a) //
        .derive(A170).to(a) //
        .derive(A42).to(a, option(A85), a, option(A86), a) //
        .derive(A85).to(a, option(A171), a, option(A172), a) //
        .derive(A171).to(a) //
        .derive(A172).to(a) //
        .derive(A86).to(a, option(A173), a, option(A174), a) //
        .derive(A173).to(a) //
        .derive(A174).to(a) //
        .derive(A10).to(a, option(A21), a, option(A22), a) //
        .derive(A21).to(a, option(A43), a, option(A44), a) //
        .derive(A43).to(a, option(A87), a, option(A88), a) //
        .derive(A87).to(a, option(A175), a, option(A176), a) //
        .derive(A175).to(a) //
        .derive(A176).to(a) //
        .derive(A88).to(a, option(A177), a, option(A178), a) //
        .derive(A177).to(a) //
        .derive(A178).to(a) //
        .derive(A44).to(a, option(A89), a, option(A90), a) //
        .derive(A89).to(a, option(A179), a, option(A180), a) //
        .derive(A179).to(a) //
        .derive(A180).to(a) //
        .derive(A90).to(a, option(A181), a, option(A182), a) //
        .derive(A181).to(a) //
        .derive(A182).to(a) //
        .derive(A22).to(a, option(A45), a, option(A46), a) //
        .derive(A45).to(a, option(A91), a, option(A92), a) //
        .derive(A91).to(a, option(A183), a, option(A184), a) //
        .derive(A183).to(a) //
        .derive(A184).to(a) //
        .derive(A92).to(a, option(A185), a, option(A186), a) //
        .derive(A185).to(a) //
        .derive(A186).to(a) //
        .derive(A46).to(a, option(A93), a, option(A94), a) //
        .derive(A93).to(a, option(A187), a, option(A188), a) //
        .derive(A187).to(a) //
        .derive(A188).to(a) //
        .derive(A94).to(a, option(A189), a, option(A190), a) //
        .derive(A189).to(a) //
        .derive(A190).to(a) //
        .derive(A2).to(a, option(A5), a, option(A6), a) //
        .derive(A5).to(a, option(A11), a, option(A12), a) //
        .derive(A11).to(a, option(A23), a, option(A24), a) //
        .derive(A23).to(a, option(A47), a, option(A48), a) //
        .derive(A47).to(a, option(A95), a, option(A96), a) //
        .derive(A95).to(a, option(A191), a, option(A192), a) //
        .derive(A191).to(a) //
        .derive(A192).to(a) //
        .derive(A96).to(a, option(A193), a, option(A194), a) //
        .derive(A193).to(a) //
        .derive(A194).to(a) //
        .derive(A48).to(a, option(A97), a, option(A98), a) //
        .derive(A97).to(a, option(A195), a, option(A196), a) //
        .derive(A195).to(a) //
        .derive(A196).to(a) //
        .derive(A98).to(a, option(A197), a, option(A198), a) //
        .derive(A197).to(a) //
        .derive(A198).to(a) //
        .derive(A24).to(a, option(A49), a, option(A50), a) //
        .derive(A49).to(a, option(A99), a, option(A100), a) //
        .derive(A99).to(a, option(A199), a, option(A200), a) //
        .derive(A199).to(a) //
        .derive(A200).to(a) //
        .derive(A100).to(a, option(A201), a, option(A202), a) //
        .derive(A201).to(a) //
        .derive(A202).to(a) //
        .derive(A50).to(a, option(A101), a, option(A102), a) //
        .derive(A101).to(a, option(A203), a, option(A204), a) //
        .derive(A203).to(a) //
        .derive(A204).to(a) //
        .derive(A102).to(a, option(A205), a, option(A206), a) //
        .derive(A205).to(a) //
        .derive(A206).to(a) //
        .derive(A12).to(a, option(A25), a, option(A26), a) //
        .derive(A25).to(a, option(A51), a, option(A52), a) //
        .derive(A51).to(a, option(A103), a, option(A104), a) //
        .derive(A103).to(a, option(A207), a, option(A208), a) //
        .derive(A207).to(a) //
        .derive(A208).to(a) //
        .derive(A104).to(a, option(A209), a, option(A210), a) //
        .derive(A209).to(a) //
        .derive(A210).to(a) //
        .derive(A52).to(a, option(A105), a, option(A106), a) //
        .derive(A105).to(a, option(A211), a, option(A212), a) //
        .derive(A211).to(a) //
        .derive(A212).to(a) //
        .derive(A106).to(a, option(A213), a, option(A214), a) //
        .derive(A213).to(a) //
        .derive(A214).to(a) //
        .derive(A26).to(a, option(A53), a, option(A54), a) //
        .derive(A53).to(a, option(A107), a, option(A108), a) //
        .derive(A107).to(a, option(A215), a, option(A216), a) //
        .derive(A215).to(a) //
        .derive(A216).to(a) //
        .derive(A108).to(a, option(A217), a, option(A218), a) //
        .derive(A217).to(a) //
        .derive(A218).to(a) //
        .derive(A54).to(a, option(A109), a, option(A110), a) //
        .derive(A109).to(a, option(A219), a, option(A220), a) //
        .derive(A219).to(a) //
        .derive(A220).to(a) //
        .derive(A110).to(a, option(A221), a, option(A222), a) //
        .derive(A221).to(a) //
        .derive(A222).to(a) //
        .derive(A6).to(a, option(A13), a, option(A14), a) //
        .derive(A13).to(a, option(A27), a, option(A28), a) //
        .derive(A27).to(a, option(A55), a, option(A56), a) //
        .derive(A55).to(a, option(A111), a, option(A112), a) //
        .derive(A111).to(a, option(A223), a, option(A224), a) //
        .derive(A223).to(a) //
        .derive(A224).to(a) //
        .derive(A112).to(a, option(A225), a, option(A226), a) //
        .derive(A225).to(a) //
        .derive(A226).to(a) //
        .derive(A56).to(a, option(A113), a, option(A114), a) //
        .derive(A113).to(a, option(A227), a, option(A228), a) //
        .derive(A227).to(a) //
        .derive(A228).to(a) //
        .derive(A114).to(a, option(A229), a, option(A230), a) //
        .derive(A229).to(a) //
        .derive(A230).to(a) //
        .derive(A28).to(a, option(A57), a, option(A58), a) //
        .derive(A57).to(a, option(A115), a, option(A116), a) //
        .derive(A115).to(a, option(A231), a, option(A232), a) //
        .derive(A231).to(a) //
        .derive(A232).to(a) //
        .derive(A116).to(a, option(A233), a, option(A234), a) //
        .derive(A233).to(a) //
        .derive(A234).to(a) //
        .derive(A58).to(a, option(A117), a, option(A118), a) //
        .derive(A117).to(a, option(A235), a, option(A236), a) //
        .derive(A235).to(a) //
        .derive(A236).to(a) //
        .derive(A118).to(a, option(A237), a, option(A238), a) //
        .derive(A237).to(a) //
        .derive(A238).to(a) //
        .derive(A14).to(a, option(A29), a, option(A30), a) //
        .derive(A29).to(a, option(A59), a, option(A60), a) //
        .derive(A59).to(a, option(A119), a, option(A120), a) //
        .derive(A119).to(a, option(A239), a, option(A240), a) //
        .derive(A239).to(a) //
        .derive(A240).to(a) //
        .derive(A120).to(a, option(A241), a, option(A242), a) //
        .derive(A241).to(a) //
        .derive(A242).to(a) //
        .derive(A60).to(a, option(A121), a, option(A122), a) //
        .derive(A121).to(a, option(A243), a, option(A244), a) //
        .derive(A243).to(a) //
        .derive(A244).to(a) //
        .derive(A122).to(a, option(A245), a, option(A246), a) //
        .derive(A245).to(a) //
        .derive(A246).to(a) //
        .derive(A30).to(a, option(A61), a, option(A62), a) //
        .derive(A61).to(a, option(A123), a, option(A124), a) //
        .derive(A123).to(a, option(A247), a, option(A248), a) //
        .derive(A247).to(a) //
        .derive(A248).to(a) //
        .derive(A124).to(a, option(A249), a, option(A250), a) //
        .derive(A249).to(a) //
        .derive(A250).to(a) //
        .derive(A62).to(a, option(A125), a, option(A126), a) //
        .derive(A125).to(a, option(A251), a, option(A252), a) //
        .derive(A251).to(a) //
        .derive(A252).to(a) //
        .derive(A126).to(a, option(A253), a, option(A254), a) //
        .derive(A253).to(a) //
        .derive(A254).to(a) //
    ;
  }
  public static void main(String[] args) throws IOException {
    new Exp2().generateGrammarFiles();
  }
}
