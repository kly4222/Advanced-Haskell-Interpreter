-- Takes a non-negative integer and calculates the factorial of that value.
-- If a negative number is entered as an argument, return -1. 
--
fact :: Integer -> Integer
fact = undefined


-- Checks the first element of 2 pairs for equality.
--
compareFst :: (a,b) -> (a,b) -> Bool
compareFst = undefined


-- Swap the contents of a list of tuples.
--
swapTuples :: [(a,b)] -> [(b,a)]
swapTuples = undefined


-- Sort a list of Strings alphabetically, removing any duplicate entries.
--
aToZ :: [String] -> [String]
aToZ = undefined


-- Returns a list comtaining the middle element(s) of a sorted list.
-- If the size of the list is odd, the returning list should contain 1 element.
-- If the size of the list is even, the returning list should contain the 2 elements in the middle.
-- Return an empty list if the sorted list is empty.
--
getMiddle :: [a] -> [a]
getMiddle = undefined


-- #########################################################
-- Some functions that may help define the functions above.
-- #########################################################

-- Return the size of a list
size :: [a] -> Integer
size = undefined

-- Swaps the contents of a pair
swap :: (a,b) -> (b,a)
swap = undefined
