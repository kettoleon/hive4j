# Hive4j

<p align="center">
  <img src="https://github.com/kettoleon/hive4j/blob/master/web/src/main/resources/static/logo128.png?raw=true" alt="Hive4j Logo"/>
</p>

Hive4j is both a java library to build and run your own LLM agents, and a spring boot web application to manage them.

## Status of the project

This project is in its early stages. It is not ready for production yet.
There are many interfaces and internal APIs that are not mature enough and will change in the future for sure.
I'm an advocate of Test Driven Development, but for this experimental MVP project I'm not following it (for now), so you won't see any tests.

## Pre-requisites

You will need to have a local ollama instance running. It is easy install, follow their instructions for your system.
In my case I have it running under a windows wsl2 (debian guest).

Make sure to run the ollama server before running this application:

````
$ ollama serve
2023/12/01 17:06:27 images.go:784: total blobs: 10
2023/12/01 17:06:27 images.go:791: total unused blobs removed: 0
2023/12/01 17:06:27 routes.go:777: Listening on 127.0.0.1:11434 (version 0.1.12)
````

## Roadmap

* :heavy_check_mark: Configure different backends, e.g. local ollama, and a remote exllama2.
* :heavy_check_mark: Pull, remove, manage LLM models available on the [OLlama](http://ollama.ai) platform.
* :hammer_and_wrench: Create/manage your own LLM agents.
* :hammer_and_wrench: Create/manage users and roles.
* :man_scientist: Be able to introspect your agents (have a chat with them, query their memories, ...).
* :man_scientist: Your agents will remember other conversations and will automatically create a minified version of the conversation to free up context space.
* :man_scientist: Your agents will be able to use tools to perform tasks.
* :thinking: Your agents will be able to learn from data you provide them.
* :thinking: Your agents will be able to look for information on the internet and keep themselves up to date.
* :thinking: Your agents will be able to interact with other agents and share information with them.
* :thinking: Your agents will be able to interact with other agents and coordinate to solve problems or perform tasks.

## Supported LLM backends

* [OLlama](http://ollama.ai) I've started with ollama for its simplicity and ease of use, to get started quickly.
* ExLlama, vLlm, etc. coming when better performance is needed.

## Main architecture

* hive4j-api: Interfaces and DTOs to be used by the rest of the modules.
* clients: Clients to connect to the different LLM backends.
* adapters: Adapters to manage the prompts and their context for different LLM models.
* hive4j-web: Spring boot web application to manage the hive.