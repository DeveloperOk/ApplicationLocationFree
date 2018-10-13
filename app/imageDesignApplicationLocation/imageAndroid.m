

inputParentFolder = 'C:\Users\PC\Desktop\image';
outputParentFolder = 'C:\Users\PC\Desktop\processed';

if(exist(outputParentFolder))
    rmdir(outputParentFolder, 's');
end

folderNamesArray = {'mipmap-hdpi', 'mipmap-mdpi', 'mipmap-xhdpi', 'mipmap-xxhdpi', 'mipmap-xxxhdpi'};

lengthOfFolderNamesArray = length(folderNamesArray);

fileNamesArray = {'ic_launcher.png', 'ic_launcher_round.png'};

lengthOfFileNamesArray = length(fileNamesArray);


for i=1:lengthOfFolderNamesArray
    
    outputFolder = strcat(outputParentFolder, '\', folderNamesArray{i});
    mkdir(outputFolder);
    
    for j=1:lengthOfFileNamesArray
    
        inputPath = strcat(inputParentFolder, '\', folderNamesArray{i}, '\', fileNamesArray{j});
        
        outputPath = strcat(outputFolder, '\', fileNamesArray{j});
    
        processImage(inputPath, outputPath);
        
    end
end

