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

/**
 *
 * @author berenger
 */
public final class Color
{
    /* Color string usable in colorize command */
    private final String colorString;
    
    private Color(String _colorString)
    {
        this.colorString = _colorString;
    }
    
    @Override
    public String toString()
    {
        return colorString;
    }
    
    public static Color   NULL    = new Color("Null");
    public static Color   RED     = new Color("Red");
    public static Color   GREEN   = new Color("Green");
    public static Color   BLUE    = new Color("Blue"); 
    public static Color   MAGENTA = new Color("Magenta");
    public static Color[] ALL     =
    {
        Color.RED,
        Color.GREEN,
        Color.BLUE,
        Color.MAGENTA
    };
}
