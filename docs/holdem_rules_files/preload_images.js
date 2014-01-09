<!-- Beginning of JavaScript -------------------

  // Use this function to preload any images which may not be loaded normally such as 
  // mouseover images
  
  function preload_images() {
    if (document.images) {
      var imgFiles = preload_images.arguments;
      var preloadArray = new Array();
      for (var i=0; i<imgFiles.length; i++) {
        preloadArray[i] = new Image;
        preloadArray[i].src = imgFiles[i];
      }
    }
  }
// -- End of JavaScript code -------------- -->