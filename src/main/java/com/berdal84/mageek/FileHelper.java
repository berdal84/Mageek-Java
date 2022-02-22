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

import java.io.File;

/**
 *
 * @author berenger
 */
public class FileHelper {

    /**
     * Delete a folder and its content recursively
     * @param _directory the folder to empty
     * @param _self if false the directory will be kept
     * @return the number of files deleted.
     */
    public static int deleteDirectoryContent(File _directory, boolean _self) {
        
        File[] content = _directory.listFiles();
        int deletedFileCount = 0;
        
        if (content != null) {
            for (File file : content) {
                if( file.isDirectory()){
                    deletedFileCount += deleteDirectoryContent(file, true);
                } else {
                    file.delete();
                    deletedFileCount++;
                }
                
            }
        }
        if (_self) {
            _directory.delete();
        }
        
        return deletedFileCount;
    }
}
