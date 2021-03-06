/*
 * The MIT License
 *
 * Copyright 2022 Berdal84.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.berdal84.mageek;

import ij.plugin.Colors;
import java.awt.Color;

/**
 * Meta object to store a color from ij (String) and java Color) point of views.
 */
public final class MetaColor
{

    /* An ImageJ color compatible String */
    private final String ijColorString;
    
    /* a java.awt.Color */
    private final Color color;

    private MetaColor(String _ijColorString, Color _color)
    {
        this.ijColorString = _ijColorString;
        this.color = _color;
    }

    /**
     * Get ImageJ color as String
     *
     * @return
     */
    public String getIJColorString()
    {
        return ijColorString;
    }

    /**
     * Get underlying java.awt.Color Object
     *
     * @return
     */
    public Color getColor()
    {
        return color;
    }

    @Override
    public String toString()
    {
        return ijColorString;
    }

    /**
     * Helper function to create a meta color from an ImageJ color string.
     * @param _ijColorString
     * @return 
     */
    private static MetaColor createFromIJColorString(String _ijColorString)
    {
        Color color = Colors.getColor(_ijColorString, null);
        MetaColor result = color == null ? null : new MetaColor(_ijColorString, color);
        return result;
    }

    /**
     * Get an existing MColor from a ImageJ color string
     * @param _ijColorString
     * @return 
     */
    public static MetaColor getWithIJString(String _ijColorString)
    {
        for (MetaColor each : All)
        {
            if (each.ijColorString.equals(_ijColorString))
            {
                return each;
            }
        }
        return MetaColor.Null;
    }

    // predefined colors    
    public static MetaColor Null = new MetaColor("None", Color.BLACK);
    public static MetaColor Red = createFromIJColorString("Red");
    public static MetaColor Green = createFromIJColorString("Green");
    public static MetaColor Blue = createFromIJColorString("Blue");
    public static MetaColor Magenta = createFromIJColorString("Magenta");

    public static MetaColor[] All =
    {
        MetaColor.Red,
        MetaColor.Green,
        MetaColor.Blue,
        MetaColor.Magenta
    };
}
