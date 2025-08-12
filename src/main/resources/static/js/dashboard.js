// CreativeFlow Dashboard JavaScript

let currentProjectType = '';

// Project creation functions
function createProject(type) {
    currentProjectType = type;
    document.getElementById('projectCategory').value = type;
    document.getElementById('projectModal').classList.remove('hidden');
    document.getElementById('projectModal').classList.add('flex');
    
    // Get AI suggestions for the project type
    getAISuggestions(type);
}

function closeModal() {
    document.getElementById('projectModal').classList.add('hidden');
    document.getElementById('projectModal').classList.remove('flex');
    document.getElementById('projectForm').reset();
}

// AI Suggestions
async function getAISuggestions(projectType) {
    try {
        const response = await fetch('/api/ai-suggestions', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: `projectType=${projectType}`
        });
        
        const suggestions = await response.json();
        displayAISuggestions(suggestions);
    } catch (error) {
        console.error('Error fetching AI suggestions:', error);
    }
}

function displayAISuggestions(suggestions) {
    // Create AI suggestions popup
    const suggestionsHtml = `
        <div class="bg-blue-50 border border-blue-200 rounded-lg p-4 mb-4">
            <h4 class="font-semibold text-blue-800 mb-2">🤖 AI Suggestions</h4>
            <div class="space-y-2 text-sm">
                <p><strong>Suggested Deadline:</strong> ${new Date(suggestions.suggestedDeadline).toLocaleDateString()}</p>
                <p><strong>Content Ideas:</strong> ${suggestions.contentIdeas.slice(0, 2).join(', ')}</p>
                <p><strong>Time Allocation:</strong> ${Object.entries(suggestions.timeAllocation).map(([key, value]) => `${key}: ${value}%`).join(', ')}</p>
            </div>
        </div>
    `;
    
    // Insert suggestions before the form
    const form = document.getElementById('projectForm');
    const existingSuggestions = form.querySelector('.ai-suggestions');
    if (existingSuggestions) {
        existingSuggestions.remove();
    }
    
    const suggestionsDiv = document.createElement('div');
    suggestionsDiv.className = 'ai-suggestions';
    suggestionsDiv.innerHTML = suggestionsHtml;
    form.insertBefore(suggestionsDiv, form.firstChild);
}

// Project form submission
document.getElementById('projectForm').addEventListener('submit', async function(e) {
    e.preventDefault();
    
    const projectData = {
        title: document.getElementById('projectTitle').value,
        description: document.getElementById('projectDescription').value,
        category: document.getElementById('projectCategory').value,
        color: getRandomColor(),
        status: 'planning'
    };
    
    try {
        console.log('Creating project with data:', projectData);
        
        // First test if server is responding
        console.log('Testing server connectivity...');
        const testResponse = await fetch('/api/projects/test');
        console.log('Server test response:', testResponse.status);
        
        const response = await fetch('/api/projects', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(projectData)
        });
        
        if (response.ok) {
            const project = await response.json();
            showNotification('Project created successfully! 🎉', 'success');
            closeModal();
            setTimeout(() => location.reload(), 1000);
        } else {
            const errorText = await response.text();
            console.error('Server error:', errorText);
            showNotification('Error creating project: ' + response.status, 'error');
        }
    } catch (error) {
        console.error('Network error:', error);
        showNotification('Network error creating project', 'error');
    }
});

// Utility functions
function getRandomColor() {
    const colors = ['bg-purple-400', 'bg-pink-400', 'bg-blue-400', 'bg-green-400', 'bg-yellow-400', 'bg-red-400'];
    return colors[Math.floor(Math.random() * colors.length)];
}

function showNotification(message, type = 'info') {
    const notification = document.createElement('div');
    notification.className = `notification bg-white border-l-4 ${
        type === 'success' ? 'border-green-400' : 
        type === 'error' ? 'border-red-400' : 'border-blue-400'
    } p-4 rounded-lg shadow-lg`;
    
    notification.innerHTML = `
        <div class="flex items-center">
            <div class="flex-shrink-0">
                <i class="fas ${
                    type === 'success' ? 'fa-check-circle text-green-400' : 
                    type === 'error' ? 'fa-exclamation-circle text-red-400' : 'fa-info-circle text-blue-400'
                }"></i>
            </div>
            <div class="ml-3">
                <p class="text-gray-800">${message}</p>
            </div>
            <div class="ml-auto">
                <button onclick="this.parentElement.parentElement.parentElement.remove()" class="text-gray-400 hover:text-gray-600">
                    <i class="fas fa-times"></i>
                </button>
            </div>
        </div>
    `;
    
    document.body.appendChild(notification);
    
    // Auto remove after 5 seconds
    setTimeout(() => {
        if (notification.parentElement) {
            notification.remove();
        }
    }, 5000);
}

// Voice recording functionality (placeholder)
function startVoiceRecording() {
    // This would integrate with Web Speech API
    showNotification('Voice recording feature coming soon! 🎤', 'info');
}

// Drag and drop functionality for templates
function initializeDragAndDrop() {
    // This would handle drag and drop for templates
    console.log('Drag and drop initialized');
}

// Initialize dashboard
document.addEventListener('DOMContentLoaded', function() {
    initializeDragAndDrop();
    
    // Add some interactive animations
    const cards = document.querySelectorAll('.hover-lift');
    cards.forEach(card => {
        card.addEventListener('mouseenter', function() {
            this.style.transform = 'translateY(-5px)';
        });
        
        card.addEventListener('mouseleave', function() {
            this.style.transform = 'translateY(0)';
        });
    });
});

// Keyboard shortcuts
document.addEventListener('keydown', function(e) {
    // Ctrl/Cmd + N for new project
    if ((e.ctrlKey || e.metaKey) && e.key === 'n') {
        e.preventDefault();
        createProject('content');
    }
    
    // Escape to close modal
    if (e.key === 'Escape') {
        closeModal();
    }
});