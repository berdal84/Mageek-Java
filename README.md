[![CI](https://github.com/berdal84/Mageek-Java/actions/workflows/ci.yml/badge.svg)](https://github.com/berdal84/Mageek-Java/actions/workflows/ci.yml)

# Mageek (Java)

Mageek is an ImageJ Plugin to process microscope images files in Fiji.

Mageek use ImageJ and Bio-Format to:
- open multi series
- project slices
- split channels (up to 4)
- apply a LUT to each channel.
- save the result as a *.tiff

Here is the result of processing a single *.czi file and putting the 4 channels in a row:

![image](https://user-images.githubusercontent.com/942052/118412778-31b48680-b69c-11eb-9c92-3dac930e49ba.png)


# How to use ?

- Download `Mageek.jar` last release from the assets [here](https://github.com/berdal84/Mageek-Java/releases/latest).
 
- Download and install Fiji from https://imagej.net/Fiji/Downloads

- Run Fiji

- On the main menu, click on `Plugins -> Install` and select `Mageek.jar` file.

![Screen Shot 2022-02-27 at 4 05 27 PM](https://user-images.githubusercontent.com/942052/155899936-2d0b487f-83ca-418f-958d-4d1586895f26.png)

![Screen Shot 2022-02-27 at 4 06 54 PM](https://user-images.githubusercontent.com/942052/155899968-aee88932-9e1d-4443-8505-cc356c034b34.png)

- Fiji must be restarted in order to take the plugin in account.

![Screen Shot 2022-02-27 at 4 07 22 PM](https://user-images.githubusercontent.com/942052/155899978-e34ee811-dbca-4695-a4e9-e5994225a79a.png)

- Search for `Mageek`, you should find it quickly by typing `Mageek` in the search field.

![Screen Shot 2022-02-27 at 4 17 47 PM](https://user-images.githubusercontent.com/942052/155900275-4353bac0-4c32-4192-8932-747688042815.png)

- Validate by hitting `Enter` or clicking `Run`.

![Screen Shot 2022-02-27 at 3 57 25 PM](https://user-images.githubusercontent.com/942052/155899624-a8e56e6c-b535-4487-9f66-e11efbd02c56.png)

- The Maggek UI appear.

![Screen Shot 2022-02-27 at 4 09 22 PM](https://user-images.githubusercontent.com/942052/155900042-46b4dd68-a56a-4255-86e1-b973129901b1.png)

- Browse to select a source directory.
- Mageek will analyse the content and show a list of files and their extensions. Only common extensions will be automatically selected. But fell free to select unselect item.

![Screen Shot 2022-02-27 at 4 11 06 PM](https://user-images.githubusercontent.com/942052/155900095-827a3093-5fde-48f9-a6b9-3f7722ab1767.png)

- Select the Z projection mode to apply on slices and the colors to apply to each channels.

![Screen Shot 2022-02-27 at 4 12 04 PM](https://user-images.githubusercontent.com/942052/155900130-db4da1e8-f291-44e3-9565-2932edb87e95.png)

- When you are ready, click on `Process` button.

![Screen Shot 2022-02-27 at 4 12 36 PM](https://user-images.githubusercontent.com/942052/155900142-9a889832-f2b7-4025-aecd-7c742c15b708.png)

*Note: process has no progress feedback, we will improve this in the future.*

Once done, you must find a new directory in your source directory named `ANALYSED`:

![Screen Shot 2022-02-27 at 4 14 24 PM](https://user-images.githubusercontent.com/942052/155900195-0b4d24be-0ce9-46f7-9cfc-251765bed217.png)

![Screen Shot 2022-02-27 at 4 15 23 PM](https://user-images.githubusercontent.com/942052/155900213-f31e0918-8870-4cf1-9d72-aa332a35e709.png)

## Credits

The source code of Mageek has been developped by [Berenger Dalle-Cort](https://www.dalle-cort.fr) with discussions with [Marcela Garita Hernandez](https://www.linkedin.com/in/marcela-garita-hernandez-pharmd-phd-ba1a2830/) in order to speed-up microscope image analysis process.

## History

This Java code is the version 2 of a previous ImageJ Macro. Source code of this version is [here](https://github.com/berdal84/mageek).
