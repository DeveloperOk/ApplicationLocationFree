

clear
clc

outputImageWidth = 500;
outputImageHeight = 1024;

rowSize = 1055;
rowStartIndex = 240;
rowEndIndex = rowStartIndex + rowSize - 1;

columnSize = 2160;
columnStartIndex = 1;
columnEndIndex = columnStartIndex + columnSize - 1;

OutputPathOfImage = 'C:\Users\PC\Desktop\output2\featureGraphic.png';

imageLeft = im2double(imread('C:\Users\PC\Desktop\screenshots\1.jpg'));
imageRight = im2double(imread('C:\Users\PC\Desktop\screenshots\8.jpg'));


image = [imageLeft imageRight];

image = image(rowStartIndex:rowEndIndex, columnStartIndex:columnEndIndex , :);

figure
imshow(image);
title('Part of Image to Process');

sizeOfImage = size(image);

inputImageWidth = sizeOfImage(1);
inputImageHeight = sizeOfImage(2);
numberOfChannels = sizeOfImage(3);

rowIndices = linspace(1, inputImageWidth, outputImageWidth);
columnIndices = linspace(1, inputImageHeight, outputImageHeight);


%Lowpass filter before downsampling
h = fspecial('average', 3);
image = imfilter(image, h);

figure
imshow(image)
title('Lowpass Filtered Image')


%Interpolation and downsampling 
[X, Y] = meshgrid([1:inputImageWidth],[1:inputImageHeight]);
[Xq, Yq] = meshgrid(rowIndices,columnIndices);


outputImage1 = interp2(X, Y, image(:,:,1)', Xq, Yq, 'cubic', 0);
outputImage2 = interp2(X, Y, image(:,:,2)', Xq, Yq, 'cubic', 0);
outputImage3 = interp2(X, Y, image(:,:,3)', Xq, Yq, 'cubic', 0);

outputImage = cat(3, outputImage1', outputImage2', outputImage3');

figure
imshow(outputImage);
title('Processed Image');

imwrite(outputImage, OutputPathOfImage);

