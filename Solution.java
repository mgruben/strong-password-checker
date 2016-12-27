
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
    String s;
    boolean needsNumber;
    boolean needsUpper;
    boolean needsLower;
    int[] seq;
    int numDeletions;
    
    private void readString() {
        int c = 1;
        char tmp = s.charAt(0);
        for (int i = 0; i < s.length(); i++) {
            if (i > 0) {
                if (s.charAt(i) == tmp) c++;
                else {
                    if (c > 2) seq[c]++;
                    c = 1;
                }
                tmp = s.charAt(i);
            }
            if (s.charAt(i) >= 'a' && s.charAt(i) <= 'z') needsLower = false;
            else if (s.charAt(i) >= 'A' && s.charAt(i) <= 'Z') needsUpper = false;
            else if (s.charAt(i) >= '0' && s.charAt(i) <= '9') needsNumber = false;
        }
        if (c > 2) seq[c]++;
    }
    
    private void spendDeletions() {
        numDeletions = (s.length() > 20) ? s.length() - 20: 0;
            int ndtemp = numDeletions;
            int lastThreeMult = 3 * ((seq.length - 1) / 3);
            for (int i = lastThreeMult; i < lastThreeMult + 3; i++) {
                int j = (i >= seq.length) ? i - 3: i;
                while (j > 2 && ndtemp > 0) {
                    if (seq[j] > 0) {
                        seq[j]--;
                        int d = Math.min((i % 3) + 1, ndtemp);
                        seq[j-d]++;
                        ndtemp -= d;
                    }
                    else j -= 3;
                }
            }
    }
    
    public int strongPasswordChecker(String s) {
        if (s == null || s.equals("")) return 6;
        this.s = s;
        
        // Initialize instance variables
        needsNumber = true;
        needsUpper = true;
        needsLower = true;
        int numAdditions = 0;
        numDeletions = 0;
        seq = new int[s.length() + 1];
        
        // Count additions needed and sequences
        readString();
        
        if (needsLower) numAdditions++;
        if (needsUpper) numAdditions++;
        if (needsNumber) numAdditions++;
        
        // Spend deletions to shorten sequences, if possible
        if (s.length() > 20) spendDeletions();
        
        // Tally number of sequence breaks needed
        int numBreaks = 0;
        for (int i = 3; i < seq.length; i++) {
            numBreaks += seq[i] * (i / 3);
        }
        
        // Consolidate breaks and additions into changes
        int numChanges = Math.max(numBreaks, numAdditions);
        
        if (s.length() < 6) {
            int numInsertions = 6 - s.length();
            numChanges = Math.max(numInsertions, numChanges);
        }
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
        // 8
        
        System.out.println(sol.strongPasswordChecker("aaaaaa1234567890123Ubefg"));
        // 4
    }
    
}
