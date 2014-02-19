{-# LANGUAGE TemplateHaskell #-}
module Basics where

import Primitives
import DataTypes
import Language.Haskell.TH

-- isP: if p>0 then ft(x1,..xm) else ff(x1,..xm)
-- isP <ft, ff> (x1, .. xm, p) = R<ff, S<ft, U_1^(m+2), .., U_m^(m+2)>>
isP m = [| \ft -> \ff -> $(r m) ff ($(addU (m+2) m) ($(s m (m+2)) ft)) |]

addU m0 cnt = [| \f -> $(addU' m0 cnt 1 [| f |]) |]
addU' m 0 id f = f
addU' m k id f = addU' m (k-1) (id+1) $ f `tapply` (u m id)

-- ifFalse<ff>(x1,..,xm,p) - if p>0 return p, ff(x1,..xm) otherwise
-- ifFalse<ff> = R<ff, S<N, U_(m+1)^(m+2)>>
ifFalse m = [| \ff -> $(r m) ff $ ($(s 1 $ m+2) n $(u (m+2) (m+1))) |]

-- P(x) - previous num to x or Zero if x==0
-- P = S<R<Z, U_1^2>, U_1^1, U_1^1>
p = $(s 2 1) ($(r 1) z $(u 3 2)) $(u 1 1) $(u 1 1)

-- Not<f>(x1,..xm) = if f(x1,..xm) > 0 then 0 else 1
-- Not<f>(x1,.. xm) = S<isP<Z, S<N,Z>>, U_1^m, f>
rfNot m = [| \f -> $(s 2 m) ($(isP 1) z ($(s 1 1) n z)) $(u m 1) f |]
