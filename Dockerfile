# Modified from
#   https://raw.githubusercontent.com/freechipsproject/chisel-bootcamp/master/Dockerfile

# First stage : setup the system and environment
FROM ubuntu:22.04

RUN \
    apt-get update && \
    DEBIAN_FRONTEND=noninteractive apt-get install -y \
        ca-certificates-java \
        curl \
        default-jdk-headless \
        gnupg \
        python3-distutils \
        build-essential \
        g++ \
        make cmake \
        graphviz \
        verilator \
        && \
    rm -rf /var/lib/apt/lists/*

ENV SCALA_VERSION=2.12.18
ENV ALMOND_VERSION=0.10.9

RUN useradd -ms /bin/bash tutorial

USER tutorial
ENV USER=tutorial
ENV HOME=/home/$USER
WORKDIR $HOME

RUN curl https://bootstrap.pypa.io/get-pip.py -o get-pip.py
ENV PATH=$HOME/.local/bin:$PATH
RUN python3 get-pip.py
RUN pip3 install notebook
RUN rm get-pip.py

RUN curl -fL "https://github.com/coursier/launchers/raw/master/cs-x86_64-pc-linux.gz" | gzip -d > cs
RUN chmod +x cs && ./cs setup -y
ENV PATH=$HOME/.local/share/coursier/bin:$PATH
RUN ./cs launch --fork almond:$ALMOND_VERSION --scala $SCALA_VERSION -- --install
RUN rm cs

USER root
ADD . /pillars/
RUN chown -R tutorial:tutorial /pillars
USER tutorial
WORKDIR /pillars

ENV JUPYTER_CONFIG_DIR=$HOME/jupyter/config
ENV JUPITER_DATA_DIR=$HOME/jupyter/data
RUN mkdir -p $JUPYTER_CONFIG_DIR/custom
RUN cp tutorial/custom.js $JUPYTER_CONFIG_DIR/custom/

# Execute a notebook to ensure Chisel is downloaded into the image for offline work
RUN make build
RUN jupyter nbconvert --to notebook --output=/tmp/3-desc-arch --execute tutorial/3-desc-arch.ipynb

WORKDIR /pillars/tutorial
EXPOSE 8888
CMD jupyter notebook --no-browser --ip 0.0.0.0 --port 8888
