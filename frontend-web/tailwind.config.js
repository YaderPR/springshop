export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}", 
  ],
  theme: {
    extend: {
      colors: {
        primary: "#000201", //  0 2 1 1 (rgba)
        secondary: "#89FE00", // 137 254 0 1 (rgba)
        tertiary: "#1E1E1E", // Dark gray
        highlight: "#FFD700",
        greendark: "#2E610B", //46,97,11,38
        accent: "#F1F1F1", // Very light gray
        background: "#F0F0F0", // Light gray
        text: "#333333", // Dark gray for text
      },
       backgroundImage: {
        "green-radial": "radial-gradient(circle, rgba(25,30,15,.4) 70%, rgba(25,30,15,1,1) 10%)",
      },
    },
  },
  plugins: [],
}
