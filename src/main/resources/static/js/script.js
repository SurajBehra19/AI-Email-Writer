// API Configuration
const API_BASE_URL = 'http://localhost:9090/api/email/generate';

// DOM Elements
const form = document.getElementById('emailForm');
const generateBtn = document.getElementById('generateBtn');
const btnText = document.querySelector('.btn-text');
const loading = document.querySelector('.loading');
const resultSection = document.getElementById('resultSection');
const resultContent = document.getElementById('resultContent');
const copyBtn = document.getElementById('copyBtn');
const errorMessage = document.getElementById('errorMessage');
const emailContent = document.getElementById('emailContent');
const emailTone = document.getElementById('emailTone');

// Event Listeners
document.addEventListener('DOMContentLoaded', initializeApp);
form.addEventListener('submit', handleFormSubmit);
copyBtn.addEventListener('click', copyToClipboard);
emailContent.addEventListener('input', autoResizeTextarea);

// Initialize Application
function initializeApp() {
    console.log('Email Assistant initialized');
    setupPlaceholder();
}

// Handle form submission
async function handleFormSubmit(e) {
    e.preventDefault();
    await generateEmail();
}

// Main function to generate email
async function generateEmail() {
    const content = emailContent.value.trim();
    const tone = emailTone.value;

    // Validation
    if (!content || !tone) {
        showError('Please fill in all fields');
        return;
    }

    // Show loading state
    setLoadingState(true);
    hideError();
    hideResult();

    try {
        const response = await fetch(API_BASE_URL, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json'
            },
            body: JSON.stringify({
                content: content,
                tone: tone
            })
        });

        if (!response.ok) {
            let errorMessage = `HTTP error! status: ${response.status}`;
            try {
                const errorText = await response.text();
                if (errorText) {
                    errorMessage = errorText;
                }
            } catch (e) {
                // Use default error message
            }
            throw new Error(errorMessage);
        }

        // Always try to read as text first (since your API returns plain text)
        const emailText = await response.text();

        // Check if it's actually JSON wrapped in text
        try {
            const parsedData = JSON.parse(emailText);
            const extractedText = parsedData.email || parsedData.content || parsedData.result || parsedData.message || emailText;
            showResult(extractedText);
        } catch (jsonError) {
            // It's plain text, use it directly
            showResult(emailText);
        }

    } catch (error) {
        console.error('Error generating email:', error);

        // Handle different types of errors
        if (error.name === 'TypeError' && error.message.includes('fetch')) {
            showError('Cannot connect to server. Please make sure your Spring Boot application is running on port 9090.');
        } else {
            showError(`Failed to generate email: ${error.message}`);
        }
    } finally {
        setLoadingState(false);
    }
}

// Set loading state
function setLoadingState(isLoading) {
    generateBtn.disabled = isLoading;
    btnText.style.display = isLoading ? 'none' : 'block';
    loading.style.display = isLoading ? 'flex' : 'none';
}

// Show generated email result
function showResult(emailText) {
    resultContent.textContent = emailText;
    resultSection.style.display = 'block';
    resultSection.classList.add('success-animation');

    // Scroll to result smoothly
    resultSection.scrollIntoView({ behavior: 'smooth', block: 'nearest' });
}

// Hide result section
function hideResult() {
    resultSection.style.display = 'none';
    resultSection.classList.remove('success-animation');
}

// Show error message
function showError(message) {
    errorMessage.textContent = message;
    errorMessage.style.display = 'block';

    // Auto-hide error after 5 seconds
    setTimeout(() => {
        hideError();
    }, 5000);
}

// Hide error message
function hideError() {
    errorMessage.style.display = 'none';
}

// Copy email to clipboard
async function copyToClipboard() {
    try {
        await navigator.clipboard.writeText(resultContent.textContent);

        // Show success feedback
        const originalText = copyBtn.textContent;
        copyBtn.textContent = '✓ Copied!';
        copyBtn.classList.add('copied');

        setTimeout(() => {
            copyBtn.textContent = originalText;
            copyBtn.classList.remove('copied');
        }, 2000);

    } catch (error) {
        console.error('Failed to copy to clipboard:', error);

        // Fallback for older browsers
        try {
            const textArea = document.createElement('textarea');
            textArea.value = resultContent.textContent;
            document.body.appendChild(textArea);
            textArea.select();
            document.execCommand('copy');
            document.body.removeChild(textArea);

            showCopySuccess();
        } catch (fallbackError) {
            showError('Failed to copy to clipboard. Please copy manually.');
        }
    }
}

// Show copy success feedback
function showCopySuccess() {
    const originalText = copyBtn.textContent;
    copyBtn.textContent = '✓ Copied!';
    copyBtn.classList.add('copied');

    setTimeout(() => {
        copyBtn.textContent = originalText;
        copyBtn.classList.remove('copied');
    }, 2000);
}

// Auto-resize textarea
function autoResizeTextarea() {
    emailContent.style.height = 'auto';
    emailContent.style.height = Math.min(emailContent.scrollHeight, 300) + 'px';
}

// Setup placeholder text
function setupPlaceholder() {
    emailContent.placeholder = `Describe what you want to write about...

Examples:
• Request a meeting with the marketing team
• Follow up on a job application
• Thank someone for their help
• Apologize for a delayed response
• Schedule a client presentation
• Request information or documents`;
}

// Utility function to validate form
function validateForm() {
    const content = emailContent.value.trim();
    const tone = emailTone.value;

    if (!content) {
        emailContent.focus();
        showError('Please describe what you want to write about');
        return false;
    }

    if (!tone) {
        emailTone.focus();
        showError('Please select a tone for your email');
        return false;
    }

    return true;
}

// Enhanced error handling for network issues
function handleNetworkError(error) {
    if (error.message.includes('Failed to fetch')) {
        return 'Cannot connect to the server. Please check if your Spring Boot application is running on port 9090.';
    } else if (error.message.includes('NetworkError')) {
        return 'Network error occurred. Please check your internet connection.';
    } else if (error.message.includes('timeout')) {
        return 'Request timed out. Please try again.';
    }
    return error.message;
}