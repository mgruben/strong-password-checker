
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
    // The given String
    String s;
        
    // The number of additions which are required
    int numAdditions;
    
    // The number of deletions which are required
    int numDeletions;
    
    // The sequences we encounter
    int[] seq;
    
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
        if (s == null || s.equals("")) return 6;
        this.s = s;
        
        // Initialize instance variables
        numAdditions = 0;
        numDeletions = 0;
        seq = new int[s.length() + 1];
        
        // Count "additions" and frequency of sequences encountered
        readString();
                
        // Spend deletions to minimize sequence breaks needed, if possible
        if (s.length() > 20) spendDeletions();
        
        // Tally number of sequence breaks needed
        int numBreaks = 0;
        for (int i = 3; i < seq.length; i++) {
            numBreaks += seq[i] * (i / 3);
        }
        
        // Consolidate breaks and additions into changes
        int numChanges = Math.max(numBreaks, numAdditions);
        
        // For too-short input, consolidate insertions and changes.
        if (s.length() < 6) {
            int numInsertions = 6 - s.length();
            numChanges = Math.max(numInsertions, numChanges);
        }
        
        // For too-long input, add the number of breaks and additions needed
        // to the number of deletions required.
        else if (s.length() > 20) {
            numChanges = numDeletions + numChanges;
        }
        
        return numChanges;
    }
    
    /**
     * Processes the given string, storing whether the String meets the
     * alphanumeric requirements, and storing the sequences of repeated
     * characters, if 3 or longer.
     */
    private void readString() {
        boolean needsNumber = true;
        boolean needsUpper = true;
        boolean needsLower = true;
        
        // The current sequence length
        int c = 1;
        char tmp = s.charAt(0);
        for (int i = 0; i < s.length(); i++) {
            if (i > 0) {
                // The sequence continues
                if (s.charAt(i) == tmp) c++;
                
                // The sequence has ended
                else {
                    if (c > 2) seq[c]++;
                    c = 1;
                    tmp = s.charAt(i);
                }
            }
            if (s.charAt(i) >= 'a' && s.charAt(i) <= 'z') needsLower = false;
            else if (s.charAt(i) >= 'A' && s.charAt(i) <= 'Z') needsUpper = false;
            else if (s.charAt(i) >= '0' && s.charAt(i) <= '9') needsNumber = false;
        }
        
        // Handle long sequences which continue to the end the given String
        if (c > 2) seq[c]++;
        
        if (needsLower) numAdditions++;
        if (needsUpper) numAdditions++;
        if (needsNumber) numAdditions++;
    }
    
    /**
     * Spends deletions to minimize the number of sequence breaks.
     * 
     * Beginning at the last index of seq which is a multiple of three,
     * count backwards through seq by threes to spend all deletions.
     * 
     * We want to start with multiples of three since we can avoid adding
     * a "break" by just deleting a single character at these indices.
     * In other words, this is the best use of our deletions.
     * 
     * Then, we start with the last index of seq which is one more than a
     * multiple of three, since we can avoid adding a "break" by just
     * deleting two characters at these indices.
     * This is the next-best use of our deletions.
     * 
     * Finally, we start with the last index of seq which is two more than a
     * multiple of three, since we can avoid adding a "break" by just
     * deleting three characters at these indices.
     * This is the most costly way to spend our deletions.
     * 
     * If we ever can't afford the full deletion at a given index, we spend
     * our remaining deletions at that index for a single sequence.
     * 
     * Counting backward allows us to spend all of our remaining deletions
     * indiscriminately, like where all sequences cost 3 deletions to remove
     * a single break.
     */
    private void spendDeletions() {
        numDeletions = s.length() - 20;
        int ndtemp = numDeletions;
        int lastThreeMult = 3 * ((seq.length - 1) / 3);
        for (int i = lastThreeMult; i < lastThreeMult + 3; i++) {
            // Handle falling off the back of seq
            int j = (i >= seq.length) ? i - 3: i;
            while (j > 2 && ndtemp > 0) {
                if (seq[j] > 0) {
                    
                    // We have one fewer sequence of length j
                    seq[j]--;
                    
                    /**
                     * Determine whether we have enough deletions remaining
                     * in order to reduce the number of needed sequence breaks
                     * by 1.
                     * 
                     * If we don't, just spend all remaining deletions.
                     * It won't affect our final "changes" tally.
                     */
                    int d = Math.min((i % 3) + 1, ndtemp);
                    
                    // We have one more sequence of length (j-d)
                    seq[j-d]++;
                    
                    // Update our spent deletions
                    ndtemp -= d;
                }
                else j -= 3;
            }
        }
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
        // 8
        
        System.out.println(sol.strongPasswordChecker("aaaaaa1234567890123Ubefg"));
        // 4
    }
    
}
