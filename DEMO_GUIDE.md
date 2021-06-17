# Demo Guide

## 1. Install Dependencies
The demo guide requires user to switch their working directory to `ethernet/sdk`.

To do so, run the command below.
```shell
cd sdk
```

### 1.1. Install virualenv
If `virtualenv` has already been installed, this step can be skipped.

```shell
sudo apt install -y python3-virtualenv
```


### 1.2. Create the virtual environment
```shell
virtualenv -p /usr/bin/python3 venv
```


### 1.3. Install dependencies in virtual environment
Start by activating the virtual environment.
```shell
source venv/bin/activate
```

Next, install the requirements as specified in `requirements.txt`
```shell
pip3 install -r python/ethernet/requirements.txt
```

Lastly, install Jupyter notebook.
```shell
pip3 install notebook
```


### 1.4. Start the notebook server
If you are running it on a local machine, use the command below:
```shell
screen -S demo_jup_server -dm jupyter notebook
```

If you would like to spin up a jupyter notebook server without caring too much about security on a 
remote server, use the command below:
```shell
screen -S demo_jup_server -dm jupyter notebook --no-browser --ip 0.0.0.0
```

### 1.5. Notebook time!
Navigate your way to `python/demo_notebook.ipynb` for the worked example on how to use EtherNet.

One can also read through the example without starting the Jupyter Notebook by navigating to:
[demo_notebook.ipynb](sdk/python/demo_notebook.ipynb)


## Demo Video
Click the image below to watch the Youtube demo video.

[![Watch the video](https://img.youtube.com/vi/DFzIya3sDfM/maxresdefault.jpg)](https://youtu.be/DFzIya3sDfM)
