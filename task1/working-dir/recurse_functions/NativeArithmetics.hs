{-# LANGUAGE TemplateHaskell #-}
module NativeArithmetics where

import Primitives
import Basics
import DataTypes
import Language.Haskell.TH


evalInteger :: (Integer -> Integer -> Integer) -> Nat -> Nat -> Nat
evalInteger f x y = fromInteger $ f (toInteger x) (toInteger y)


-- + = R<U_1^1, S<N, U_3^3>>
(@+) :: Nat -> Nat -> Nat
(@+) = evalInteger (+) 
infixl 6 @+

-- - = R<U_1^1, S<P, U_3^3>>
(@-) :: Nat -> Nat -> Nat
(@-) = evalInteger (-) 
infixl 6 @-

-- * = R<U_1^1, S<+, U_1^3, U_3^3>>
(@*) :: Nat -> Nat -> Nat
(@*) = evalInteger (*)
infixl 7 @*

-- @^ = R<U_1^2, S<*, U_1^4, U_4^4>>
(@^) :: Nat -> Nat -> Nat
(@^) = evalInteger (^)
infixr 8 @^


-- div(x,y) = S<isP<S<Z, U_1^2>,g>, U_1^2, U_2^2, S<P, S<-, U_2^2, U_1^2>>>
-- g = S<R<S<Z, U_1^2>, g'>, U_1^2, U_2^2, S<N, U_1^2>>
-- //g works fine, we put it in bigger construction for a bit of performance in
-- // some cases
-- //iterate a in [0..x]
-- g'(x,y,a,prev) = ifFalse<S<g'', U_1^3, U_2^3, U_3^3,U_2^3>>
-- //prev - if we succeed with one of previous a then a, 0 otherwise
-- //if prev >0 then prev else g''(x,y,a,y)
-- g'' = R<S<Z, U_1^3>, ifFalse<g'''>>
-- //iterate b in [0..y-1]. if one y succeed, it's beeing returned, otherwise
-- //returning g'''(x,y,a,b)
-- g''' = S<isP<U_1^1, Z>, U_3^4, S<=, U_1^4, S<+, S<*, U_2^4, U_3^4>, U_4^4>>>
-- //if (x=a*y+b), b<y then return a else return 0
(@/) :: Nat -> Nat -> Nat
(@/) = evalInteger (div)
infixl 7 @/

-- mod = S<-, U_1^2, S<*, U_2^2, S<div, U_1^2, U_2^2>>>
(@%) :: Nat -> Nat -> Nat
(@%) = evalInteger (mod)
infixl 7 @%

