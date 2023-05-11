import logging
import os
import tarfile
import zipfile

logger = logging.getLogger('file_util')
logging.root.setLevel(logging.DEBUG)

def get_filePath_fileName_fileExt(fileUrl):
    """
    获取文件路径， 文件名， 后缀名
    :param fileUrl:
    :return:
    """
    filepath, tmpfilename = os.path.split(fileUrl)
    shotname, extension = os.path.splitext(tmpfilename)
    return filepath, shotname, extension


def findThisFiles(filename, dir):

    destpaths = []

    dirpaths = []
    dirpaths.append(dir)

    for d in dirpaths:
        if os.path.isfile(d):
            # 判断文件是不是advert.json
            (filepath, shotname, extension) = get_filePath_fileName_fileExt(d)
            fname = "%s%s" % (shotname, extension)
            if fname == filename:
                destpaths.append(d)
        elif os.path.isdir(d):
            cdirs = os.listdir(d)
            for f in cdirs:
                p = "%s/%s" % (d, f)
                dirpaths.append(p)

    return destpaths

def decompression(caseZipPath, destDir=None):

    dir = None
    try:
        # 解压
        unzipSucc = True
        dir = destDir
        if dir and not os.path.exists(dir):
            os.mkdir(dir)

        if tarfile.is_tarfile(caseZipPath):
            dir = caseZipPath.replace(r'.tar.gz', '')
            t = tarfile.open(caseZipPath)
            t.extractall(path=dir)
            t.close()
        elif zipfile.is_zipfile(caseZipPath):
            zip_file = zipfile.ZipFile(caseZipPath)
            dir = caseZipPath.replace('.zip', "")
            for names in zip_file.namelist():
                zip_file.extract(names, dir)
            zip_file.close()
        else:
            unzipSucc = False
            logger.error("unzip fail, caseZipPath : %s" % (caseZipPath))

    except Exception as e:
        print(e)
        logger.error("unzip fail, caseZipPath : %s" % (caseZipPath))
        unzipSucc = False

    return (unzipSucc, dir)


def get_zip_file(input_path, result):
    """
    对目录进行深度优先遍历
    :param input_path:
    :param result:
    :return:
    """
    files = os.listdir(input_path)
    for file in files:
        if os.path.isdir(input_path + '/' + file):
            get_zip_file(input_path + '/' + file, result)
        else:
            result.append(input_path + '/' + file)



def get_zip_file(input_path, result):
    """
    对目录进行深度优先遍历
    :param input_path:
    :param result:
    :return:
    """
    files = os.listdir(input_path)
    for file in files:
        if os.path.isdir(input_path + '/' + file):
            get_zip_file(input_path + '/' + file, result)
        else:
            result.append(input_path + '/' + file)

def compression(srcDir, destZipPath:str=None):
    if destZipPath == None:
        destZipPath = "{}.zip".format(srcDir)
    f = zipfile.ZipFile(destZipPath, 'w')
    for parent, dirnames, filenames in os.walk(srcDir):
        for filename in filenames:
            pathfile = os.path.join(parent, filename)
            f.write(pathfile, filename)
    f.close()
    return destZipPath


def getFileContent(txtFilePath):

    txt = None
    if os.path.exists(txtFilePath):
        with open(txtFilePath, r'r') as f:
            txt = f.read()
    return txt

def setFileContent(txtFilePath, txt):

    with open(txtFilePath, r'w', encoding="utf-8") as f:
        f.write(txt)
