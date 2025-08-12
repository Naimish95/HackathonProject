// Voice-to-text functionality for all text inputs

let recognition;
let currentInputId;

function startVoiceToText(inputId) {
    if (!('webkitSpeechRecognition' in window) && !('SpeechRecognition' in window)) {
        showNotification('Speech recognition not supported in this browser', 'error');
        return;
    }
    
    currentInputId = inputId;
    recognition = new (window.SpeechRecognition || window.webkitSpeechRecognition)();
    recognition.continuous = false;
    recognition.interimResults = false;
    recognition.lang = 'en-US';
    
    recognition.onstart = function() {
        showVoiceModal();
    };
    
    recognition.onresult = function(event) {
        const transcript = event.results[0][0].transcript;
        showTranscriptModal(transcript);
    };
    
    recognition.onerror = function(event) {
        closeVoiceModal();
        showNotification('Speech recognition error: ' + event.error, 'error');
    };
    
    recognition.onend = function() {
        closeVoiceModal();
    };
    
    recognition.start();
}

function showVoiceModal() {
    const modal = document.createElement('div');
    modal.id = 'voiceModal';
    modal.className = 'fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50';
    modal.innerHTML = `
        <div class="bg-white rounded-2xl p-8 text-center">
            <div class="w-16 h-16 bg-red-500 rounded-full flex items-center justify-center mx-auto mb-4 animate-pulse">
                <i class="fas fa-microphone text-white text-2xl"></i>
            </div>
            <h3 class="text-xl font-semibold text-gray-800 mb-2">Listening...</h3>
            <p class="text-gray-600">Speak now to convert speech to text</p>
            <button onclick="stopVoiceRecognition()" class="mt-4 bg-gray-300 text-gray-700 px-6 py-2 rounded-lg hover:bg-gray-400 transition-colors">
                Stop
            </button>
        </div>
    `;
    document.body.appendChild(modal);
}

function showTranscriptModal(transcript) {
    closeVoiceModal();
    
    const modal = document.createElement('div');
    modal.id = 'transcriptModal';
    modal.className = 'fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50';
    modal.innerHTML = `
        <div class="bg-white rounded-2xl p-8 max-w-md w-full mx-4">
            <h3 class="text-xl font-semibold text-gray-800 mb-4">Voice to Text Result</h3>
            <div class="bg-gray-50 p-4 rounded-lg mb-4">
                <p class="text-gray-800">${transcript}</p>
            </div>
            <div class="flex space-x-3">
                <button onclick="copyTranscript('${transcript.replace(/'/g, "\\'")}')" class="flex-1 bg-blue-600 text-white py-2 rounded-lg hover:bg-blue-700 transition-colors">
                    <i class="fas fa-copy mr-2"></i>Copy
                </button>
                <button onclick="insertTranscript('${transcript.replace(/'/g, "\\'")}')" class="flex-1 bg-green-600 text-white py-2 rounded-lg hover:bg-green-700 transition-colors">
                    <i class="fas fa-plus mr-2"></i>Insert
                </button>
                <button onclick="closeTranscriptModal()" class="flex-1 bg-gray-300 text-gray-700 py-2 rounded-lg hover:bg-gray-400 transition-colors">
                    Cancel
                </button>
            </div>
        </div>
    `;
    document.body.appendChild(modal);
}

function copyTranscript(transcript) {
    navigator.clipboard.writeText(transcript).then(() => {
        showNotification('Text copied to clipboard! 📋', 'success');
        closeTranscriptModal();
    });
}

function insertTranscript(transcript) {
    const input = document.getElementById(currentInputId);
    if (input) {
        if (input.tagName === 'TEXTAREA' || input.tagName === 'INPUT') {
            input.value += (input.value ? ' ' : '') + transcript;
        }
    }
    closeTranscriptModal();
    showNotification('Text inserted! 🎤', 'success');
}

function stopVoiceRecognition() {
    if (recognition) {
        recognition.stop();
    }
    closeVoiceModal();
}

function closeVoiceModal() {
    const modal = document.getElementById('voiceModal');
    if (modal) {
        modal.remove();
    }
}

function closeTranscriptModal() {
    const modal = document.getElementById('transcriptModal');
    if (modal) {
        modal.remove();
    }
}

// Add voice-to-text buttons to all text inputs on page load
function addVoiceToTextButtons() {
    const textInputs = document.querySelectorAll('input[type="text"], textarea');
    
    textInputs.forEach(input => {
        if (!input.id) {
            input.id = 'input_' + Math.random().toString(36).substr(2, 9);
        }
        
        const parent = input.parentElement;
        if (!parent.classList.contains('relative')) {
            parent.classList.add('relative');
        }
        
        // Check if voice button already exists
        if (!parent.querySelector('.voice-btn')) {
            const voiceBtn = document.createElement('button');
            voiceBtn.type = 'button';
            voiceBtn.className = 'voice-btn absolute right-2 top-2 text-gray-400 hover:text-purple-600 transition-colors';
            voiceBtn.innerHTML = '<i class="fas fa-microphone"></i>';
            voiceBtn.onclick = () => startVoiceToText(input.id);
            
            parent.appendChild(voiceBtn);
        }
    });
}

// Initialize voice-to-text on page load
document.addEventListener('DOMContentLoaded', function() {
    addVoiceToTextButtons();
});

// Re-initialize when new content is added dynamically
const observer = new MutationObserver(function(mutations) {
    mutations.forEach(function(mutation) {
        if (mutation.type === 'childList') {
            addVoiceToTextButtons();
        }
    });
});

observer.observe(document.body, {
    childList: true,
    subtree: true
});