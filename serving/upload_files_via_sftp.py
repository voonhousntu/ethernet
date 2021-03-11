import sys
import time
import pysftp
from typing import List

def upload_files_via_sftp(
    host: str,
    username: str,
    password: str,
    ethernet_work_dir: str,
    files: List[str]
):
    if "~/" in ethernet_work_dir:
        # Build home folder dir of user account runing ethernet
        home_path = "/home/{username}".format(username=username)
        ethernet_work_dir = ethernet_work_dir.replace("~/", "")
        cd_path = "{home_path}/{ethernet_work_dir}".format(
            home_path=home_path,
            ethernet_work_dir=ethernet_work_dir
        )
    else:
        cd_path = ethernet_work_dir

    # Change the path to the ethernet working directory
    with pysftp.Connection(host, username=username, password=password) as sftp:
        print("Connected to {host} successfully".format(host=host)
        with sftp.cd(cd_path):
            # Upload file to public/ on remote
            for file in files:
                print("Uploading: {file}".format(file=file))
                sftp.put(file)
                print("Uploaded: {file} to {dir}\n".format(file=file, dir=cd_path))


if __name__ == "__main__":
    # First argument is the python file name at index 0
    host = sys.argv[1]
    username = sys.argv[2]
    password = sys.argv[3]
    ethernet_work_dir = sys.argv[4]
    files_str = sys.argv[5]

    # Files in `files_str` must addressed with absolute path in the user's local filesystem
    # Cleanup `files_str`
    files = files_str.split(",")

    start = time.time()
    upload_files_via_sftp(host, username, password, ethernet_work_dir, files)
    end = time.time()
    time_taken_in_seconds = end - start
    print("Time elapsed: {seconds}".format(seconds=time_taken_in_seconds))
