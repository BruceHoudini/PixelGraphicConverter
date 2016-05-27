# PixelGraphicConverter
<h2>Toolkit for modifying gifs and images.</h2>
Currently supporting compression, expansion, creative ascii and color palette conversions.<br>
My objective is to create a simple program which can convert a given image or gif into a limited ascii or pixelart representation using reduced color palettes from archaic computers and video game consoles. While the truth is that I am creating this for my own enjoyment the "intended" user, should one need to be specified, are game development hobbyists looking to more easily convert images into a specialized graphics style evocative of a particular era of technology for the purpose of facilitating <b>new age fun</b> with a <b>vintage feel.</b>

<h3>Updates</h3>
In reverse chronological order.
<ul>
<li>Changed name from UselessGifTextGifConverter to PixelGraphicConverter as the old name is no longer accurate and also because I have been told that self-deprication is not an attractive trait in a man.
<li>Added Floyd-Steinberg dithering for color-palette reduction.</li>
<li>Added alternate color palettes and color replacement options (64 color NES model and 15bit SNES model).</li>
<li>Added different pixel replacement, resizing, and image generation options independent of ascii image generation.</li>
<li>Added variable background colors for ascii gif generation along with transparency option.</li>
<li>Removed necessity for intermediate file creation, only input file and output destination required.</li>
<li>Added in program ability to expand gifs into frames and then recombine modified frames into sigular gif.</li>
<li>Program no longer converts gifs into text files.</li>
<li>Program no longer converts gifs into text files into gifs.</li>
</ul>



<h3>ToDo</h3>
<ol type = "I">
  <li>Add additional console specific palette and pixeldepth conversion options.
    <ol type = "i">
      <li>Original Gameboy 2bit green grayscale</li>
      <li>Gameboy to Gameboy Color special color options (when placing GB cartridge into GBC) </li>
      <li>Atari 2600 </li>
      <li>Microsoft Windows default 16 and 20 color palette</li>
      <li>Apple Macintosh default 16 color palette</li>
    </ol>
  </li>
  <li>Add additional general pixeldepth conversion options.
    <ol type = "i">
      <li>2-bit monochrome</li>
      <li>3-bit RGB</li>
      <li>6-bit RGB</li>
      <li>9-bit RGB</li>
      <li>12-bit RGB</li>
      <li>16-bit RGB</li>
    </ol>
  </li>
  <li>Implement adaptive palette selection, optimized palette generation, color quantization etc.
    <ol type = "i">
      <li>Median cut algorithm for 24bit RGB to 16-color palette.</li>
      <li>Kohenon net for automated palette optimization.</li>
      <li>Creation of 3D Voronoi diagram for subsequent color matching.</li>
    </ol>
  </li>
  <li>Improve and expand dithering effects and options</li>
    <ol type = "i">
      <li>ordered dithering using bayer matrices of variable size and pattern</li>
      <li>threshold dithering</li>
    </ol>
  </li>
  <li>Various organization and restructring to create more logical method interaction and reduced coupling.
    <ol type = "i">
      <li>ditherFS() should be called after populateColoryArray() and act upon the array rather than the original image.</li>
      <li>Primary class ImageToText should be renamed to accurately reflect its current function rather than its archaic function</li>
      <li>Various other methods should be renamed to accurately reflect their current functions.</li>
      <li>Place into new method commented out code responsible for creating ascii representations of images as text files</li>
    </ol>
  </li>
  <li>Improve image to ascii functionality.
    <ol type = "i">
      <li>Non-arbitrary symbol choice.</li>
      <li>Reimplement option to create text file from image.</li>
      <li>Create algorithm for dynamic symbol placement based upon geometry of image.</li>
      <li>Second pass analysis of ascii method which modifies symbols based upon structure of current ascii representation.</li>
    </ol>
  </li>
  <li>Optimization
    <ol type = "i">
      <li>Identify and remove redundancy or unnecessary comparisons or actions left-over from previous project interations</li>
      <li>Implement multithreading for populateColorArray() and convertToBlockGraphc(). Multithread where possible elswhere</li>
      <li>Research and implement faster image creation and traversal techniques. Identify and address performance bottlenecks.</li>
    </ol>
  </li>
  <li>Create simple GUI
    <ol type = "i">
      <li>If possible, design to be accessed from and used in browswer from website: www.positionovertime.com</li>
      <li>Create simple executable.</li>
      <li>Honestly, I have no idea what this entails. I know nothing about java tools for creating interactive graphics.</li>
    </ol>
  </li>
</ol>
