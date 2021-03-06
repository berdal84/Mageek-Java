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
import java.util.ArrayList;
import org.apache.commons.io.FilenameUtils;

/**
 * Some File relative functions used in Mageek
 */
public class FileHelper
{

    public static String getFormattedExtension(File _file)
    {
        return formatExtension(FilenameUtils.getExtension(_file.getName()));
    }

    public static String formatExtension(String _extension)
    {
        return String.format("*.%s", _extension);
    }

    /**
     * Get file extensions from a file list.
     *
     * @param _files
     * @return
     */
    public static ArrayList<String> getFileExtensions(ArrayList<File> _files)
    {
        ArrayList<String> result = new ArrayList();

        _files.forEach((File _eachFile) ->
        {
            String extension = getFormattedExtension(_eachFile);
            if (extension != null && !result.contains(extension))
            {
                result.add(extension);
            }
        });

        return result;
    }

    /**
     * Get files from a directory recursively or not
     *
     * @param _directory
     * @param _recursively
     * @return
     */
    public static ArrayList<File> getFiles(File _directory, boolean _recursively, String _ignoreFolderName)
    {
        ArrayList<File> result = new ArrayList<>();
        File[] content = _directory.listFiles();

        if (content != null)
        {
            for (File file : content)
            {
                if (file.isDirectory() && !file.getName().equals(_ignoreFolderName) )
                {
                    if (_recursively)
                    {
                        ArrayList<File> subFolderFiles = getFiles(file, true, _ignoreFolderName);
                        result.addAll(subFolderFiles);
                    }
                }
                else
                {
                    result.add(file);
                }

            }
        }

        return result;
    }

    /**
     * Delete a folder and its content recursively
     *
     * @param _directory the folder to empty
     * @param _self if false the directory will be kept
     * @return the number of files deleted.
     */
    public static int deleteDirectoryContent(File _directory, boolean _self)
    {

        File[] content = _directory.listFiles();
        int deletedFileCount = 0;

        if (content != null)
        {
            for (File file : content)
            {
                if (file.isDirectory())
                {
                    deletedFileCount += deleteDirectoryContent(file, true);
                }
                else
                {
                    file.delete();
                    deletedFileCount++;
                }

            }
        }

        if (_self)
        {
            _directory.delete();
        }

        return deletedFileCount;
    }
}
