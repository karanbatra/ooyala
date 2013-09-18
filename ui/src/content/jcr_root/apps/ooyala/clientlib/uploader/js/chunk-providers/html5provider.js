(function($){

  /**
   * Provision the browseElement with the file selector
   * @private
   * @note:Technique to add the invisible file selector taken from Resummable.js (https://github.com/23/resumable.js)
   * */
  var initHTMLFileSelector = function(){
    //This function is suppposed to be called using the context of the owner Object, 
    //which in this case is the Ooyala.Client.HTMLUplaoder.
    var that = this;
    var sel = document.createElement("input");
    sel.type = "file";

    this.browseButton.style.display = "inline-block";
    this.browseButton.style.position = "relative";
    sel.style.position = "absolute";
    sel.style.top = sel.style.left = sel.style.bottom = sel.style.right = 0;
    sel.style.opacity = 0;
    sel.style.cursor = "pointer";

    this.browseButton.appendChild(sel);

    sel.addEventListener("change", function(e){
      that.file = sel.files[0];
      that.dispatchEvent("fileSelected");
    }, false);
  };

  Ooyala.Client.HTML5ChunkProvider = function(file, browseButton){
    Ooyala.Client.EventDispatcher.call(this);
    this.file = file;
    this.data = "";
    this.browseButton = browseButton;
    //Call private method to initialize the file selector button.
    initHTMLFileSelector.call(this);
  };

  $.extend(Ooyala.Client.HTML5ChunkProvider.prototype, new Ooyala.Client.EventDispatcher(), {
    getChunk: function(startByte, endByte){
      if(!this.file){
        throw new Error("No file has been selected yet.");
      }
      var that = this;
      var sliceFunctionName = (this.file.mozSlice ? 'mozSlice' : 'slice');

      this.data = this.file[sliceFunctionName](startByte, endByte);
      this.dispatchEvent("complete");
    }
  });
}).call(this, jQuery);
