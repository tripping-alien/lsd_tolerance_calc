# LSD Tolerance Model: Time-based Dosing Calculator

## 🌈 Project Overview

The **LSD Tolerance Model** is an educational web application designed to estimate the required dose adjustment for LSD based on the time elapsed since the last dose. It leverages a commonly referenced pharmacological model of rapid tolerance and receptor downregulation, which suggests a return to baseline sensitivity over approximately seven days.

The app features a responsive design, multi-language support (English, Russian, Hebrew), and a hypnotic, real-time WebGL background animation built with Three.js to complement the psychedelic theme.

## 📄 Table of Contents

1.  [🚀 Live Demo](#-live-demo)
2.  [✨ Features](#-features)
3.  [🧠 The Tolerance Model (Pharmacology)](#-the-tolerance-model-pharmacology)
4.  [⚠️ Essential Disclaimer](#-essential-disclaimer)
5.  [💻 Technology Stack](#-technology-stack)
6.  [⚙️ Development & Usage](#-development--usage)

-----

## 🚀 Live Demo

You can view the live, fully responsive application here:

👉 **[https://lsd-tolerance-calc.onrender.com/](https://lsd-tolerance-calc.onrender.com/)**

## ✨ Features

  * **Time-Based Calculation:** Determines the required compensatory dose based on the user's last dose time and desired current effect.
  * **Multilingual Support:** Supports English (`EN`), Russian (`RU`), and Hebrew (`HE`), including dynamic RTL (Right-to-Left) layout switching.
  * **Psychedelic WebGL Background:** Features a custom, low-overhead fragment shader (GLSL) powered by Three.js to generate a dynamic, mesmerizing rainbow pattern.
  * **Fully Responsive:** Optimized for mobile, tablet, and desktop viewing, ensuring the calculator card is centered and readable against the animated background.
  * **Clear Status Feedback:** Provides a tolerance multiplier and a status (e.g., "High Tolerance," "Low Tolerance") for easy understanding.

## 🧠 The Tolerance Model (Pharmacology)

The calculator uses a common, step-wise approximation model for 5-HT2A receptor downregulation and upregulation recovery. This model estimates the tolerance multiplier needed to achieve the *desired effect* based on the time difference between doses.

| Time Elapsed | Tolerance Multiplier | Status |
| :----------- | :------------------- | :----- |
| \< 12 hours   | \~3.5x                | High   |
| \< 24 hours   | \~2.5x                | High   |
| \< 48 hours   | \~1.8x                | Moderate |
| \< 72 hours   | \~1.4x                | Moderate |
| \< 96 hours   | \~1.2x                | Low    |
| \< 120 hours  | \~1.1x                | Low    |
| \< 144 hours  | \~1.05x               | Trace  |
| \< 168 hours  | \~1.02x               | Trace  |
| 7+ Days      | 1.0x (Full Reset)    | Baseline |

The required dose is calculated as:
$$\text{Required Dose} = \text{Desired Effect Dose} \times \text{Tolerance Multiplier}$$

-----

## ⚠️ Essential Disclaimer

This tool is designed **exclusively for educational purposes** and is based on generalized, simplified pharmacological models and historical community data.

**DO NOT USE THIS CALCULATOR TO DETERMINE ACTUAL DOSING.** Individual tolerance and metabolic factors vary widely. This tool is not a substitute for professional medical advice. The developer assumes no responsibility for misuse.

## 💻 Technology Stack

  * **HTML5:** The core structure of the application.
  * **Tailwind CSS:** Used for utility-first, responsive styling and layout.
  * **JavaScript (ES6+):** Handles all calculation logic, UI updates, and language switching.
  * **Three.js:** Utilized for initializing the WebGL context and managing the scene.
  * **GLSL (Fragment Shader):** Provides the custom, dynamic rainbow animation.

## ⚙️ Development & Usage

The entire application is contained within a single `index.html` file, making it extremely easy to deploy or run locally.

To run this project locally:

1.  Clone the repository:
    ```bash
    git clone [your-repo-link]
    ```
2.  Navigate to the project directory.
3.  Open `index.html` in your web browser.
