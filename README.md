<!-- ⚠️ This README has been generated from the file(s) "blueprint.md" link - https://github.com/andreasbm/readme ⚠️--><p align="center">
  <img src="https://mir-s3-cdn-cf.behance.net/project_modules/disp/a685e7437236.5600962f5b4c4.jpg" alt="Logo" width="150" height="150" />
</p>
<h1 align="center">XML to PDF</h1>
 <p align="center">
		<a href="https://github.com/Gkemon/XML-to-PDF-generator"><img alt="Maintained" src="https://img.shields.io/badge/Maintained%3F-yes-green.svg" height="20"/></a>
	<a href="https://github.com/Gkemon/XML-to-PDF-generator"><img alt="Maintained" src="https://cdn.rawgit.com/sindresorhus/awesome/d7305f38d29fed78fa85652e3a63e154dd8e8829/media/badge.svg" height="20"/></a>
	</p>

<p align="center">
  <b>Automatically generate PDF file from XML file or Java's View object in Android</b></br>
  <sub>Make PDF from Android layout resourses (e.g - R.layout.myLayout), Java's view ids or directly views objects <sub>
</p>

<br />


<p align="center">
  <!-- GIF <img src="https://raw.githubusercontent.com/andreasbm/readme/master/assets/demo.gif" alt="Demo" width="800" /> --!>
</p>

* **Simple**: Extremely simple to use. For using <b>Step Builder Design Patten</b> undernath,here IDE greatly helps developers to complete the steps for creating a PDF from XMLs.
* **Powerful**: Customize almost everything.
* **Transparent**: It shows logs,success-responses, failure-responses , that's why developer will nofity any event inside the process. 

<details>
<summary>📖 Table of Contents</summary>
<br />

[![-----------------------------------------------------](https://raw.githubusercontent.com/andreasbm/readme/master/assets/lines/colored.png)](#table-of-contents)

## ➤ Table of Contents

* [➤ Installation](#-installation)
* [➤ Getting Started (quick)](#-getting-started-quick)
* [➤ License](#-license-1)
</details>


[![-----------------------------------------------------](https://raw.githubusercontent.com/andreasbm/readme/master/assets/lines/colored.png)](#installation)

## ➤ Installation

```javascript
npm install @appnest/readme -D
```

If you don't want to install anything you can use the `npx @appnest/readme generate` command instead.

[![-----------------------------------------------------](https://raw.githubusercontent.com/andreasbm/readme/master/assets/lines/colored.png)](#getting-started-quick)



## ➤ Getting Started (slower)

This getting started guide is a little bit longer, but will give you some superpowers. Spend a minute reading this getting started guide and you'll have the best README file in your town very soon.

### Blueprint

First you need to create a `blueprint.md` file. This blueprint is going to be the blueprint for the `README.md` file we will generate later.

Let's start simple. In order to get values from your `package.json` file injected into the README file we use the mustache syntax (`{{ .. }}`). Let's say your `package.json` file looks like this:

```json
{
  "name": "@appnest/readme",
  "version": "1.2.5"
}
```

To get the `name` and `version` into your README file you will need to write `{{ pkg.name }}` and `{{ pkg.version }}` in your `blueprint.md` file like this:

```markdown
Welcome to {{ pkg.name }}. This is version {{ pkg.version }}!
```

When running `node_modules/.bin/readme generate` the file `README.md` will be generated with the following contents:

```markdown
Welcome to @appnest/readme. This is version 1.2.5.
```



### Configuration

To configure this library you'll need to create a `blueprint.json` file. This file is the configuration for the templates we are going to take a look at in the next section. If you want to interpolate values from the configuration file into your README file you can simply reference them without a scope. Eg. if you have the field "link" in your `blueprint.json` you can write `{{ link }}` to reference it.

Great. Now that we have the basics covered, let's continue and see how you can use templates!

[![-----------------------------------------------------](https://raw.githubusercontent.com/andreasbm/readme/master/assets/lines/colored.png)](#templates)


### Logo

The logo template adds a logo to your readme and looks like this:

<p align="center">
  <img src="https://raw.githubusercontent.com/andreasbm/readme/master/assets/logo-shadow.png" alt="Logo" width="150" height="150" />
</p>

Use the placeholder `{{ template:logo }}` to stamp it. You will need to add the `logo` field to your `blueprint.json`. The logo field requires an `src` field. Optionally you can provide values for `width`, `height` and `alt`. Below is an example on how to add the data for the logo template.

```json
{
  "logo": {
    "src": "https://raw.githubusercontent.com/andreasbm/readme/master/assets/logo-shadow.png",
    "width": "150"
  }
}
```



## ➤ Contributors
	

| [<img alt="Andreas Mehlsen" src="https://avatars1.githubusercontent.com/u/6267397?s=460&v=4" width="100">](https://twitter.com/andreasmehlsen) | [<img alt="You?" src="https://joeschmoe.io/api/v1/random" width="100">](https://github.com/andreasbm/readme/blob/master/CONTRIBUTING.md) |
|:--------------------------------------------------:|:--------------------------------------------------:|
| [Andreas Mehlsen](https://twitter.com/andreasmehlsen) | [You?](https://github.com/andreasbm/readme/blob/master/CONTRIBUTING.md) |
| 🔥                                               |                                                  |


Use the `{{ template:contributors }}` placeholder to stamp it. Let's sa To use this template your are required to add the `contributors` array to your `package.json` file like this. Only the `name` field is required.

```json
{
  "contributors": [
    {
      "name": "Andreas Mehlsen",
      "email": "hello@example.com",
      "url": "https://twitter.com/andreasmehlsen",
      "img": "https://avatars1.githubusercontent.com/u/6267397?s=460&v=4",
      "info": [
        "🔥"
      ]
    },
    {
      "name": "You?",
      "img": "https://joeschmoe.io/api/v1/random",
      "url": "https://github.com/andreasbm/readme/blob/master/CONTRIBUTING.md"
    }
  ]
}
```

Take note of the `info` array. That one is really exciting! Here you can add lines describing the contributors - for example the role of accomplishments. Take a look [here](https://allcontributors.org/docs/en/emoji-key) for more inspiration of what you could put into the info array.

### License

The license template adds a license section and looks like this:


[![-----------------------------------------------------](https://raw.githubusercontent.com/andreasbm/readme/master/assets/lines/colored.png)](#license)


