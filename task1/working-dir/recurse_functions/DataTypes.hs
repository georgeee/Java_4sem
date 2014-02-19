module DataTypes where

type Nat = Integer


{-data Nat = Zero | Succ Nat | Negate Nat-}
    {-deriving (Eq, Ord)-}

{-instance Show Nat where-}
    {-show x = "@" ++ (show $ toInteger x)-}

{-instance Num Nat where-}
    {-(+) a b = fromInteger $ (toInteger a) + (toInteger b) -}
    {-(*) a b = fromInteger $ (toInteger a) * (toInteger b) -}
    
    {-fromInteger n | n > 0 = Succ $ fromInteger $ n-1-}
                  {-| n == 0 = Zero-}
                  {-| n < 0 = negate $ fromInteger $ -n-}
    
    {-abs x = fromInteger $ abs $ toInteger x-}
    {-signum x = fromInteger $ signum $ toInteger x-}
    
    {-negate Zero = Zero-}
    {-negate (Negate x) = x-}
    {-negate n = Negate n-}

{-instance Integral Nat where-}
    {-toInteger (Negate a) = negate $ toInteger a-}
    {-toInteger (Succ x) = (toInteger x) + 1-}
    {-toInteger Zero = 0-}

    {-quotRem x y = let (q,r) = quotRem (toInteger x) (toInteger y) in (fromInteger q, fromInteger r)-}

{-instance Real Nat where-}
    {-toRational x = toRational $ toInteger x-}

{-instance Enum Nat where-}
    {-toEnum x | x > 0   = Succ $ toEnum $ x-1-}
             {-| x == 0  = Zero-}
             {-| x < 0   = negate $ toEnum (-x)-}
    {-fromEnum Zero = 0-}
    {-fromEnum (Succ x) = (fromEnum x) + 1-}
    {-fromEnum (Negate x) = -(fromEnum x)-}


