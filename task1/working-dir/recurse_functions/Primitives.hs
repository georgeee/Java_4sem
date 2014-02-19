{-# LANGUAGE TemplateHaskell #-}
module Primitives(z,n,u,r,s,m,tapply) where

import Language.Haskell.TH
import DataTypes
import Data.Function

z :: Nat -> Nat
z = \_ -> 0

n :: Nat -> Nat
n = (+) 1

u :: Int -> Int -> Q Exp
u n k = u' n k [| Nothing |]

u'' :: Maybe a -> a
u'' Nothing = error "wrong u template arguments, should be: 1<=k<=n"
u'' (Just x) = x

u' :: Int -> Int -> Q Exp -> Q Exp
u' 0 _ code = [| u'' $code  |]
u' n 0 code = [| \_ -> $(u' (n-1) 0 code) |]
u' n 1 code = [| \x -> $(u' (n-1) 0 [| Just x |] ) |]
u' n k code = [| \_ -> $(u' (n-1) (k-1) code) |]

tapply a b = [| $a $b |]
infixr 1 `tapply`

s :: Int -> Int -> Q Exp
s n m = [| \f -> $(s' n m [| f |] [] ) |]
s' 0 0 f gl = foldl tapply f (reverse gl)
s' 0 m f gl = [| \xi -> $(s' 0 (m-1) f (map (`tapply` [| xi |]) gl)) |]
s' n m f gl = [| \gi -> $(s' (n-1) m f (([| gi |]):gl) ) |]

r n = [| \f -> \g -> $(r' n [| f |] [| g |]) |]
r' 0 f g = [| \y -> r'' $f $g y |]
r' n f g = [| \xi -> $(r' (n-1) [| $f xi |] [| $g xi |]) |]
r'' f g 0 = f
r'' f g y = g (y-1) (r'' f g $ y-1)

m n = [| \f -> $(m' n [| f |]) |]
m' 0 f = [| m'' $f |]
m' n f = [| \xi -> $(m' (n-1) [| $f xi |]) |]

m'' :: (Nat -> Nat) -> Nat
m'' f = m''' f [0..]
m''' f (n:l)
    | (f (fromInteger n :: Nat)) == 0  = fromInteger n :: Nat
    | otherwise   = m''' f l

