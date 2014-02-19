{-# LANGUAGE TemplateHaskell #-}
module Akkerman where

import Primitives
import Basics
import NativeFunctions
import NativeArithmetics
import DataTypes
import Language.Haskell.TH

--- Stack infrastructure

-- one = S<N, Z>
one = ($(s 1 1) n z)
-- two = S<N, one>
two = ($(s 1 1) n one)
-- one2 = S<one, U^2_1>
one2 = $(s 1 2) one $(u 2 1)
-- one3 = S<one, U^3_1>
one3 = $(s 1 3) one $(u 3 1)
-- two2 = S<two, U^2_1>
two2 = $(s 1 2) two $(u 2 1)


-- size = S<plog, two, U^1_1>
size = $(s 2 1) plog two $(u 1 1)

-- push = S<*, U^2_2, S<*, two2, S<^, S<nthPrime, S<+, two2, S<size, U^2_2>>>,
--             U^2_1>>>
push = $(s 2 2) (@*) $(u 2 2) $ $(s 2 2) (@*) two2 $ $(s 2 2) (@^) newBasement $(u 2 1)
        where newBasement = $(s 1 2) nthPrime $ $(s 2 2) (@+) two2 $ $(s 1 2) size $(u 2 2)

-- top = S<plog, S<nthPrime, S<+, two, size>>, U^1_1>
top = $(s 2 1) plog ($(s 1 1) nthPrime $ $(s 2 1) (@+) one size) $(u 1 1)

-- pop = S</, S</, U^1_1, S<^, S<nthPrime, S<+, one, size>>, top>>, two>
pop = $(s 2 1) (@/) pop' two
    where pop' = $(s 2 1) (@/) $(u 1 1) $ $(s 2 1) (@^) pop'' top
          pop'' = $(s 1 1) nthPrime $ $(s 2 1) (@+) one size

printStack 1 = "[]"
printStack st = (show $ top st) ++ " : " ++ (printStack $ pop st)

-- akkerman = M<S<-, S<size, testSteps>, S<Z, U^3_1>>>
akkerman = $(s 1 2) top $ $(s 3 2) testSteps $(u 2 1) $(u 2 2) akkM
   where    akkM = $(m 2) $ $(s 2 3) (@-) ($(s 1 3) size testSteps) one3
            testSteps = $(r 2) initStack step
            initStack = $(s 2 2) push $(u 2 2) $ $(s 2 2) push $(u 2 1) one2
            step = $(s 3 4) step' getM getN getSt
            getN = $(s 1 4) top $(u 4 4)
            getM = $(s 1 4) top $ $(s 1 4) pop $(u 4 4)
            getSt = $(s 1 4) pop $ $(s 1 4) pop $(u 4 4)
            step' = $(s 4 3) ($(isP 3) step2' step1') $(u 3 1) $(u 3 2) $(u 3 3) $(u 3 1)
            step1' = $(s 2 3) push ($(s 2 3) (@+) one3 $(u 3 2)) $(u 3 3)
            step2' = $(s 4 3) ($(isP 3) step4' step3') $(u 3 1) $(u 3 2) $(u 3 3) $(u 3 2)
            step3' = $(s 2 3) push one3 $ $(s 2 3) push ($(s 2 3) (@-) $(u 3 1) one3) $(u 3 3)
            step4' = $(s 2 3) push ($(s 2 3) (@-) $(u 3 2) one3) $ $(s 2 3) push $(u 3 1) $ $(s 2 3) push ($(s 2 3) (@-) $(u 3 1) one3) $(u 3 3)

