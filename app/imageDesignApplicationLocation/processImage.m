function [  ] = processImage( pathOfInputImage, pathOfOutputImage )

    imageRead = imread(pathOfInputImage);

    imageReadDouble = im2double(imageRead);

    imageProcessed = imageReadDouble;

    sizeOfimageReadDouble = size(imageProcessed);

    dimensionOne = sizeOfimageReadDouble(1);
    dimensionTwo = sizeOfimageReadDouble(2);


    ratio = 0.1;
    
    rowIndexTop = round(ratio*dimensionOne);
    rowIndexBottom = round((1 - ratio)*dimensionOne);
    
    columnIndexTop = round(ratio*dimensionTwo);
    columnIndexBottom = round((1 - ratio)*dimensionTwo);

    
    ratioLocation = 0.3;
    rowIndex = round(ratioLocation*dimensionOne);
    columnIndex = round(ratioLocation*dimensionTwo);
    
    ratioLocation2 = 0.34;
    rowIndex2 = round(ratioLocation2*dimensionOne);
    columnIndex2 = round(ratioLocation2*dimensionTwo);
    
    
    for i=1:dimensionOne
        for j=1:dimensionTwo
            
            
            imageProcessed(i,j,1) = 0.0;
            imageProcessed(i,j,2) = 0.0;
            imageProcessed(i,j,3) = 0.0;
        
            
            if(i == rowIndexTop)
                        imageProcessed(i,j,1) = 1.0;
                        imageProcessed(i,j,2) = 1.0;
                        imageProcessed(i,j,3) = 1.0;   
            end
            
            if(i == rowIndexBottom)
                        imageProcessed(i,j,1) = 1.0;
                        imageProcessed(i,j,2) = 1.0;
                        imageProcessed(i,j,3) = 1.0;   
            end
            
            if(j == columnIndexTop)
                        imageProcessed(i,j,1) = 1.0;
                        imageProcessed(i,j,2) = 1.0;
                        imageProcessed(i,j,3) = 1.0;   
            end
            
            if(j == columnIndexBottom)
                        imageProcessed(i,j,1) = 1.0;
                        imageProcessed(i,j,2) = 1.0;
                        imageProcessed(i,j,3) = 1.0;   
            end
            
            
            if(i == rowIndex)
                        imageProcessed(i,j,1) = 1.0;
                        imageProcessed(i,j,2) = 1.0;
                        imageProcessed(i,j,3) = 0.0;   
            end
            
            if(i == rowIndex2)
                        imageProcessed(i,j,1) = 1.0;
                        imageProcessed(i,j,2) = 1.0;
                        imageProcessed(i,j,3) = 0.0;   
            end
            
            
            if(j == columnIndex)
                        imageProcessed(i,j,1) = 1.0;
                        imageProcessed(i,j,2) = 0.0;
                        imageProcessed(i,j,3) = 0.0;   
            end

            if(j == columnIndex2)
                        imageProcessed(i,j,1) = 1.0;
                        imageProcessed(i,j,2) = 0.0;
                        imageProcessed(i,j,3) = 0.0;   
            end
            
            
        end
    end
              

    figure
    imshow(imageProcessed);

    imwrite(imageProcessed, pathOfOutputImage);

end

