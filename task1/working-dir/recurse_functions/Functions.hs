{-# LANGUAGE TemplateHaskell #-}
module Functions where

import Arithmetics
import Primitives
import Basics
import DataTypes
import Language.Haskell.TH


-- diff = S<ifFalse<->, U_2^2, U_1^2, S<-, U_1^2, U_2^2>>
-- diff(x,y) = |x-y|
nDiff = $(s 3 2) ($(ifFalse 2) (@-)) $(u 2 2) $(u 2 1) ($(s 2 2) (@-) $(u 2 1) $(u 2 2))
infixl 6 `nDiff`

-- isPrime = Not<isNotPrime>
isPrime = $(rfNot 1) isNotPrime

-- isNotPrime' = S<R<Z, ifFalse<S<mod,U_1^2, S<S<N,N>, U_2^2>>>>, U_1^1, S<P, P>>
-- //correct test for x>=2
-- isNotPrime = S<isP<isNotPrime', S<N, Z>>, U_1^1, P>
-- //correct for any x
isNotPrime = test
    where test' = $(s 2 1) ($(r 1) z ($(ifFalse 2) $ $(s 2 2) ($(rfNot 2) (@%)) $(u 2 1) ($(s 1 2) ($(s 1 1) n n) $(u 2 2)))) $(u 1 1) ($(s 1 1) p p)
          test  = $(s 2 1) ($(isP 1) test' ($(s 1 1) n z)) $(u 1 1) p

-- nthPrime = M<g>
-- g = S<diff, U_1^2, S<S<R<Z,g''>,U_1^1,U_1^1>,U_2^2>
-- g'' = S<isP<N, U_1^1>, U_3^3, S<isPrime, S<N, U_2^3>>>
{-nthPrime :: Nat -> Nat-}
nthPrime = $(m 1) g
    where g = $(s 2 2) nDiff $(u 2 1) ($(s 2 2) ($(r 1) z g'') $(u 2 2) $(u 2 2))
          g'' = $(s 2 3) ($(isP 1) n $(u 1 1)) $(u 3 3) ($(s 1 3) isPrime ($(s 1 3) n $(u 3 2)))


-- plog = M<g'>
-- g'(p, x, r) = S<Not<mod>, U_2^3, S<^, U_1^3, S<N, U_3^3>>>
-- r - max log, when x mod p^r = 0
{-plog :: Nat -> Nat -> Nat-}
plog = $(m 2) $ $(s 2 3) ($(rfNot 2) (@%)) $(u 3 2) $ $(s 2 3) (@^) $(u 3 1) $ $(s 1 3) n $(u 3 3)


{--- (plog 2 192) works really long-}
{--- so that I've tried to measure, how will it be using native integer mod and pow implementations-}
{--- so that I've created plogNative-}
{-plogNative = $(m 2) $ $(s 2 3) ($(rfNot 2) $ evalInteger mod) $(u 3 2) $ $(s 2 3) (evalInteger (^)) $(u 3 1) $ $(s 1 3) n $(u 3 3)-}
{-evalInteger :: (Integer -> Integer -> Integer) -> Nat -> Nat -> Nat-}
{-evalInteger f x y = fromInteger $ f (toInteger x) (toInteger y)-}





