const express = require("express");
const fs = require("fs");
const path = require("path");

const app = express();
const PORT = 8000;

// Path to your log file
const logFilePath = path.join(__dirname, "../logs/application.log");

// Endpoint to serve logs
app.get("/logs", (req, res) => {
    fs.readFile(logFilePath, "utf8", (err, data) => {
        if (err) {
            return res.status(500).json({ error: "Failed to read log file", details: err.message });
        }
        // Split logs into lines and send as JSON array
        res.json(data.split("\n").filter(line => line.trim() !== ""));
    });
});

// Start server
app.listen(PORT, () => {
    console.log(`Server running at http://localhost:${PORT}/logs`);
});
