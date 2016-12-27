/*
 * Copyright (C) 2016 Michael <GrubenM@GMail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/**
 *
 * @author Michael <GrubenM@GMail.com>
 */
public class Solution {
    public int strongPasswordChecker(String s) {
        if (s == null) return 6;
        
        boolean needsNumber = true;
        boolean needsUpper = true;
        boolean needsLower = true;
        
        // The number of characters in sequence
        int c = 1;
        
        // The number of breaks needed in sequences of 3 or more
        int numBreaks = 0;
        
        // Initialize our comparison character
        char tmp = s.charAt(0);
        
        // Iterate over characters in the String
        for (int i = 0; i < s.length(); i++) {
            
            // Determine sequence length by comparing to the previous character
            if (i > 0) {
                
                // Increase the current sequence length
                if (s.charAt(i) == tmp) c++;
                
                /**
                 * The sequence has ended.
                 * Count the number of breaks needed and
                 * reset the sequence count.
                 */
                else {
                    numBreaks += c / 3;
                    c = 1;
                }
                
                /**
                 * Assign the current character to tmp for comparison in the
                 * next iteration.
                 */
                tmp = s.charAt(i);
            }
            
            // Determine whether conditions have been met
            if (s.charAt(i) >= 'a' && s.charAt(i) <= 'z') needsLower = false;
            if (s.charAt(i) >= 'A' && s.charAt(i) <= 'Z') needsUpper = false;
            if (s.charAt(i) >= '0' && s.charAt(i) <= '9') needsNumber = false;
        }
        
        // For sequences that end the string, count the number of breaks needed.
        numBreaks += c / 3;
        
        // Tally the number of additions (not necessarily insertions!) needed
        int numAdditions = 0;
        if (needsLower) numAdditions++;
        if (needsUpper) numAdditions++;
        if (needsNumber) numAdditions++;
        
        System.out.println("Needs " + numAdditions + " additions");
        System.out.println("Needs Lower: " + needsLower);
        System.out.println("Needs Upper: " + needsUpper);
        System.out.println("Needs Number: " + needsNumber);
        System.out.println("Needs " + numBreaks + " breaks of repeated character");
        
        int numInsertions = (s.length() < 6) ? 6 - s.length(): 0;
        
        int numChanges = Math.max(numInsertions, Math.max(numBreaks, numAdditions));
        return numChanges;
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Solution sol = new Solution();
        System.out.println(sol.strongPasswordChecker("0123456789"));
        // needsLower = true; needsUpper = true;
        
        System.out.println(sol.strongPasswordChecker("abcdefghijklmnopqrstuvwxyz"));
        // needsUpper = true; needsNumber = true;
        
        System.out.println(sol.strongPasswordChecker("ABCDEFGHIJKLMNOPQRSTUVWXYZ"));
        // needsNumber = true; needsLower = true;
        
        System.out.println(sol.strongPasswordChecker("aaaaA"));
        // 2 e.g. add a number at 2, change 4 to capital
        
        System.out.println(sol.strongPasswordChecker("aaaaa"));
        // 2 e.g. add a number at 2, change 4 to capital
        
        System.out.println(sol.strongPasswordChecker("aaaaaaaaaaaaaaaaaaaaa"));
        // 7 e.g. one deletion, one change to capital, one change to number, and 5 changes from 'a'
        
        System.out.println(sol.strongPasswordChecker("$$$"));
        // 3 e.g. three insertions (which can satisfy requirements, and break sequence)
        
        System.out.println(sol.strongPasswordChecker("Aa1Aa1Aa1Aa1Aa1Aa1zzAa1Aa1Aa1Aa1Aa1Aa1zzAa1Aa1Aa1Aa1Aa1Aa1zz"));
        // 40 (e.g. 40 deletions)
    }
    
}
