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
public class ColorPreset
{

    private String name;
    private MetaColor[] colors;

    ColorPreset()
    {
        name = "";
        MetaColor[] colors =
        {
            MetaColor.Null,
            MetaColor.Null,
            MetaColor.Null,
            MetaColor.Null
        };
        this.colors = colors;
    }

    ColorPreset(String _name)
    {
        name = _name;
        MetaColor[] colors =
        {
            MetaColor.Null,
            MetaColor.Null,
            MetaColor.Null,
            MetaColor.Null
        };
        this.colors = colors;
    }

    ColorPreset(
            String _name,
            MetaColor _color0,
            MetaColor _color1,
            MetaColor _color2,
            MetaColor _color3
    )
    {
        this.name = _name;
        this.colors = new MetaColor[]
        {
            _color0,
            _color1,
            _color2,
            _color3
        };
    }

    ;
    
    ColorPreset(final ColorPreset _other)
    {
        this.name = _other.name;
        this.colors = new MetaColor[]
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

    final MetaColor getMetaColorAt(int _index)
    {
        return colors[_index];
    }
    
    final void setMetaColorAt(int _index, MetaColor _color)
    {
        colors[_index] = _color;
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
        colors[i] = MetaColor.getWithIJString(_ijColorString);
    }
}
