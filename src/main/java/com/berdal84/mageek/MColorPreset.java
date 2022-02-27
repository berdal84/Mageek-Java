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

import java.awt.Color;

/**
 * Color preset (4 colors per preset) identified by a name
 */
public class MColorPreset
{

    private String name;
    private MColor[] colors;

    MColorPreset()
    {
        name = "";
        MColor[] colors =
        {
            MColor.Null,
            MColor.Null,
            MColor.Null,
            MColor.Null
        };
        this.colors = colors;
    }

    MColorPreset(String _name)
    {
        name = _name;
        MColor[] colors =
        {
            MColor.Null,
            MColor.Null,
            MColor.Null,
            MColor.Null
        };
        this.colors = colors;
    }

    MColorPreset(
            String _name,
            MColor _color0,
            MColor _color1,
            MColor _color2,
            MColor _color3
    )
    {
        this.name = _name;
        this.colors = new MColor[]
        {
            _color0,
            _color1,
            _color2,
            _color3
        };
    }

    ;
    
    MColorPreset(final MColorPreset _other)
    {
        this.name = _other.name;
        this.colors = new MColor[]
        {
            _other.colors[0],
            _other.colors[1],
            _other.colors[2],
            _other.colors[3]
        };
    }

    ;
    
    final Color getColorAt(int _index)
    {
        return colors[_index].getColor();
    }

    final MColor getMetaColorAt(int _index)
    {
        return colors[_index];
    }

    final String getIJColorStringAt(int _index)
    {
        return colors[_index].toString();
    }

    final String getName()
    {
        return name;
    }

    void setIJColorStringAt(int i, String _ijColorString)
    {
        colors[i] = MColor.getWithIJString(_ijColorString);
    }
}
