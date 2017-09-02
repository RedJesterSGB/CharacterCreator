package com.character.creator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

/**
 *
 * @author Alexander J Schmidt
 * @version 1.0
 */
public class RandomNameGenerator
{

    private HashMap<String, List<Character>> chains = new HashMap<String, List<Character>>();
    private List<String> samples = new ArrayList<String>();
    private List<String> used = new ArrayList<String>();
    private Random rnd = new Random();
    private int order;
    private int minLength;

    /**
     *
     * @param path
     *            The path to the text document containing the list of names
     *            used for name generation.
     * @param order
     *            The number of previous letters looked at each time a new
     *            letter is selected
     * @param minLength
     *            the minimum length a name can be
     *
     *            Creates a Random Name Generator that can be used to generate
     *            random names based on the names from a given list
     */
    public RandomNameGenerator(String path, int order, int minLength)
    {
        if (order < 1)
            order = 1;
        if (minLength < 1)
            minLength = 1;

        this.order = order;
        this.minLength = minLength;

        FileHandle file = Gdx.files.internal(path);
        String[] sampleNames = file.readString().split("\\r?\\n");

        for (String line : sampleNames)
        {
            String[] tokens = line.split(",");
            for (String word : tokens)
            {
                String upper = word.trim().toUpperCase();
                if (upper.length() < order + 1)
                    continue;
                samples.add(upper);
            }
        }

        for (String word : samples)
        {
            for (int letter = 0; letter < word.length() - order; letter++)
            {
                String token = word.substring(letter, letter + order);
                List<Character> entry = null;
                if (chains.containsKey(token))
                    entry = chains.get(token);
                else
                {
                    entry = new ArrayList<Character>();
                    chains.put(token, entry);
                }
                entry.add(word.charAt(letter + order));
            }
        }
    }

    /**
     * Next Name generates a new random name based on the list and parameters
     * put in initially, each time you create a new name that name is added to a
     * list so this method wont return that name again.
     *
     * @return a newly generated Name
     */
    public String NextName()
    {
        String s = "";
        do
        {
            int n = rnd.nextInt(samples.size());
            int nameLength = samples.get(n).length();
            int r = rnd.nextInt(samples.get(n).length() - order);
            s = samples.get(n).substring(r, r + order);
            while (s.length() < nameLength)
            {
                String token = s.substring(s.length() - order, s.length());
                char c = GetLetter(token);
                if (c != '?')
                    s += GetLetter(token);
                else
                    break;
            }

            if (s.contains(" "))
            {
                String[] tokens = s.split(" ");
                s = "";
                for (int t = 0; t < tokens.length; t++)
                {
                    if (tokens[t] == "")
                        continue;
                    if (tokens[t].length() == 1)
                        tokens[t] = tokens[t].toUpperCase();
                    else
                        tokens[t] = tokens[t].substring(0, 1)
                                + tokens[t].substring(1).toLowerCase();
                    if (s != "")
                        s += " ";
                    s += tokens[t];
                }
            }
            else
                s = s.substring(0, 1) + s.substring(1).toLowerCase();
        } while (used.contains(s) || s.length() < minLength);
        used.add(s);
        return s;

    }

    /**
     * Resets the used names list so those names can possibly be generated
     * again.
     */
    public void Reset()
    {
        used.clear();
    }

    private char GetLetter(String token)
    {
        if (!chains.containsKey(token))
            return '?';
        List<Character> letters = chains.get(token);
        int n = rnd.nextInt(letters.size());
        return letters.get(n);
    }

}
