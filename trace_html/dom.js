window.onload = function()
{
    let imgs = document.querySelectorAll("img.graph");

    for(let img of imgs)
    {
        (function(img) 
        {
             // If you change the image's margin in the CSS file, remember to change the placeholder's margin here too
             let placeHolder = document.createElement("img");
             let r = img.getBoundingClientRect();
             placeHolder.style = "margin-left:10px; margin-top:10px; width: " + r.width + "px; height: " + r.height + "px; z-index:-100;";
    
            img.addEventListener("mouseenter", function() {
                const viewWidth = Math.max(
                  document.documentElement.clientWidth || 0
                , window.innerWidth || 0
                );

                let r2 = img.getBoundingClientRect();
                                
                let mw = " max-width: none; "
                if(r2.left + img.naturalWidth > viewWidth)
                {
                    mw = " max-width: " + (viewWidth - r2.left) + ";";
                }
                
                img.style = "position:absolute; left:" + (r2.left + window.pageXOffset)+ "px; top:" + (r2.top + window.pageYOffset) +"px; border:2px solid black;" + mw;
                img.after(placeHolder);
 
            });
            
            img.addEventListener("mouseleave",  function() {
                placeHolder.remove();
                img.style = "";
            });
            
        })(img);
    }
}
