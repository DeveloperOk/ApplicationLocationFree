

clear
clc

imageWidth = 512;
imageHeight = 512;
imageNumberOfChannels = 3;

outputPathOfTempImage = 'C:\Users\PC\Desktop\output\tempImage.png';
pathOfOutputImageWithoutAlpha = 'C:\Users\PC\Desktop\output\hiResIconWithoutAlpha.png';
pathOfOutputImage = 'C:\Users\PC\Desktop\output\hiResIcon.png';

tempImage = 1.0*zeros(imageWidth, imageHeight, imageNumberOfChannels);
imwrite(tempImage, outputPathOfTempImage);


pathOfInputImage = outputPathOfTempImage;

processImage( pathOfInputImage, pathOfOutputImageWithoutAlpha );

hiResImageWithoutAlpha = imread(pathOfOutputImageWithoutAlpha);

Alpha = 1.0*ones(imageWidth, imageHeight);
imwrite(hiResImageWithoutAlpha, pathOfOutputImage, 'Alpha', Alpha);

