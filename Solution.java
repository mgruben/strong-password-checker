
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
    
    /**
     * Given a String s, the candidate password, returns the minimum number
     * of single-action changes required for that password to be "strong".
     * 
     * In order to be "strong," a password must:
     * (1) be between 6 and 20 characters in length, inclusive,
     * (2) contain at least one lowercase letter,
     * (3) contain at least one uppercase letter.
     * (4) contain at least one number.
     * (5) not contain a sequence of 3 or more repeated characters
     * 
     * The possible single-action changes are:
     * (a) Delete a character,
     * (b) Insert a character,
     * (c) Replace a character with another character.
     * 
     * Ex. "abc12" -> 1 since the password is missing one character, and that
     *      character can be an uppercase letter.
     * 
     * Ex. "aaabbb" -> 2 since we can change the middle 'a' and 'b' to 'H' and '1'.
     * 
     * Ex. "Aa1Aa1Aa1Aa1Aa1Aa1zzAa1Aa1Aa1Aa1Aa1Aa1zzAa1Aa1Aa1Aa1Aa1Aa1zz" ->
     *      40, since the password meets the strong criteria except that it is
     *      60 characters long, so we must delete 40 characters.
     * 
     * Ex. "$$$$$$" -> 3, since it's missing lowercase, uppercase, and a number,
     *      and we can distribute those replacements to break the sequence of 6.
     * 
     * @param s the given string
     * @return the minimum number of changes to have a strong password
     */
    public int strongPasswordChecker(String s) {
        
        // Check for empty input
        if (s == null || s.equals("")) return 6;
        
        /**
         * Our "additions" variables.
         * 
         * We will set these to "false" as we discover characters which are
         * lowercase, uppercase, or numbers.
         * 
         * As used in this solution, "additions" refers to how many of the
         * three alphanumeric requirements still need to be satisfied.
         * 
         * This is a separate concept from "insertions", which is how many
         * characters need to be added to reach the minimum password length.
         */
        boolean needsNumber = true;
        boolean needsUpper = true;
        boolean needsLower = true;
        int numAdditions = 0;
        
        /**
         * The number of deletions which are required.
         * 
         * A "deletion" is the removal of a character from the password.
         */
        int numDeletions = (s.length() > 20) ? s.length() - 20: 0;
                
        // Initialize the number of characters in sequence
        int c = 1;
        
        /**
         * The sequences we encounter.
         * 
         * We will use this to spend our deletions (if any) in the most
         * efficient way possible.
         * 
         * E.g. If you have three deletions and three sequences of length three,
         * spend one deletion per sequence to avoid having to take another
         * change-action to break the sequences.
         * 
         * The indices in this array refer to the length of the sequence.
         */
        int[] seq = new int[s.length() + 1];
                
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
                 * 
                 * Store the current length in seq and reset the current length.
                 */
                else {
                    if (c > 2) seq[c]++;
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
        
        // For sequences that end the string, add the sequence to our array.
        if (c > 2) seq[c]++;
        
        // Tally the number of additions (not necessarily insertions!) needed
        if (needsLower) numAdditions++;
        if (needsUpper) numAdditions++;
        if (needsNumber) numAdditions++;
        
        // Do our clever sequence shortening
        int ndtemp = numDeletions;
        
        /**
         * Beginning at the last index of seq which is a multiple of three,
         * count backwards through seq by threes to decrement sequences.
         * 
         * We want to start with multiples of three since we can avoid adding
         * a "break" by just deleting a single character.
         * 
         * Then, we check the last index of seq which is one more than a
         * multiple of three, since we can avoid adding a "break" by just
         * deleting two characters.
         * 
         * Finally, we check the last index of seq which is two more than a
         * multiple of three, since we can avoid adding a "break" by just
         * deleting three characters.  This is the most costly way to spend
         * our deletions.
         */
        int lastThreeMult = 3 * ((seq.length - 1) / 3);
        for (int i = lastThreeMult; i < lastThreeMult + 3; i++) {
            // Handle falling off the end of seq
            int j = (i >= seq.length) ? i - 3: i;
            
            // Iterate downward by threes through seq
            while (j > 2 && ndtemp > 0) {
                
                // If there is a sequence of length j
                if (seq[j] > 0) {
                    
                    // Decrement the number of sequences of length j by 1
                    seq[j]--;
                    
                    /**
                     * Determine whether we have enough deletions to spend
                     * to reduce the number of needed breaks by 1, otherwise
                     * just spend all of our remaining deletions.
                     */
                    int d = Math.min((i % 3) + 1, ndtemp);
                    
                    // Increment the number of sequences of length j-d by 1
                    seq[j-d]++;
                    
                    // Update our spent deletions
                    ndtemp -= d;
                }
                else j -= 3;
            }
        }
        
        // Calculate the number of breaks
        int numBreaks = 0;
        for (int i = 3; i < seq.length; i++) {
            /**
             * We need a single break for a sequence of three and for a 
             * sequence of five, but two breaks for a sequence of six.
             * 
             * Accordingly, we need 1 break for every (sequenceLength) / 3.
             * 
             * Handle multiple sequences of the same length by multiplying by
             * the number of sequences of that length
             */
            numBreaks += seq[i] * (i / 3);
        }
        
        /**
         * Consolidate breaks and additions, if possible.
         * 
         * Note that we can't have fewer breaks than are required, or fewer
         * additions than are required, but that every break can serve as an
         * addition, and vice versa.
         * 
         * Accordingly, the number of changes we need is the max of the two.
         */
        int numChanges = Math.max(numBreaks, numAdditions);
        
        /**
         * Calculate number of changes for short input.
         * 
         * Note that we can't have fewer insertions than are required, or fewer
         * changes (see above) than are required, but that every insertion can
         * serve as a change, and vice versa.
         * 
         * Accordingly, the number of changes we need is the max of the two.
         */
        if (s.length() < 6) {
            int numInsertions = 6 - s.length();
            numChanges = Math.max(numInsertions, numChanges);
        }
        
        /**
         * Calculate number of changes for long input.
         * 
         * Note that we already reduced numChanges above by the amount of
         * deletions we could cleverly spend.
         * 
         * Accordingly, we need to now add those deletions back in, since they
         * are required for length.
         */
        else if (s.length() > 20) {
            numChanges = numDeletions + numChanges;
        }
        return numChanges;
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Solution sol = new Solution();
        
        System.out.println(sol.strongPasswordChecker(""));
        // 6 insertions
        
        System.out.println(sol.strongPasswordChecker("0123456789"));
        // 2 changes or additions; needsLower = true; needsUpper = true;
        
        System.out.println(sol.strongPasswordChecker("abcdefghijklmnopqrstuvwxyz"));
        // 8; 6 deletions and 2 changes; needsUpper = true; needsNumber = true;
        
        System.out.println(sol.strongPasswordChecker("ABCDEFGHIJKLMNOPQRSTUVWXYZ"));
        // 8; 6 deletions and 2 changes; needsNumber = true; needsLower = true;
        
        System.out.println(sol.strongPasswordChecker("aaaaA"));
        // 1 e.g. add a number at 2
        
        System.out.println(sol.strongPasswordChecker("aaaaa"));
        // 2 e.g. add a number at 2, change 4 to capital
        
        System.out.println(sol.strongPasswordChecker("aaaaaaaaaaaaaaaaaaaaa"));
        // 7 e.g. one deletion, one change to capital, one change to number, and 5 changes from 'a'
        
        System.out.println(sol.strongPasswordChecker("$$$"));
        // 3 e.g. three insertions (which can satisfy requirements, and break sequence)
        
        System.out.println(sol.strongPasswordChecker("Aa1Aa1Aa1Aa1Aa1Aa1zzAa1Aa1Aa1Aa1Aa1Aa1zzAa1Aa1Aa1Aa1Aa1Aa1zz"));
        // 40 (e.g. 40 deletions)
        
        System.out.println(sol.strongPasswordChecker("ABABABABABABABABABAB1"));
        // 2
        
        System.out.println(sol.strongPasswordChecker("aaaaaaaaaaaaaaaaaaaaa"));
        // 7
        
        System.out.println(sol.strongPasswordChecker("1010101010aaaB10101010"));
        // 2
        
        System.out.println(sol.strongPasswordChecker("aaaabbaaabbaaa123456A"));
        // 3
        
        System.out.println(sol.strongPasswordChecker("aaa111"));
        // 2
        
        System.out.println(sol.strongPasswordChecker("AAAAAABBBBBB123456789a"));
        // 4
        
        System.out.println(sol.strongPasswordChecker("aaaaabbbbbccccccddddddA1"));
        // 6
        
        System.out.println(sol.strongPasswordChecker("aaaaaa1234567890123Ubefg"));
        // 4
    }
    
}
