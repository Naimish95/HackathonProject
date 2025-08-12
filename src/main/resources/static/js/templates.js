// Templates page functionality

// Filter templates by category
function filterTemplates(category) {
    const templateCards = document.querySelectorAll('.template-card');
    const filterButtons = document.querySelectorAll('.template-filter');
    
    // Update active filter
    filterButtons.forEach(btn => btn.classList.remove('active', 'bg-purple-100', 'text-purple-700'));
    event.target.classList.add('active', 'bg-purple-100', 'text-purple-700');
    
    templateCards.forEach(card => {
        const cardCategory = card.getAttribute('data-category');
        
        if (category === 'all' || cardCategory === category) {
            card.style.display = 'block';
        } else {
            card.style.display = 'none';
        }
    });
}

// Use template to create project
function useTemplate(templateType) {
    const templateData = getTemplateData(templateType);
    
    // Create project from template
    fetch('/api/projects', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(templateData)
    })
    .then(response => response.json())
    .then(project => {
        showNotification(`Project created from ${templateType} template! 🎉`, 'success');
        
        // Increment template usage
        fetch(`/api/templates/${templateType}/use`, {
            method: 'POST'
        });
        
        // Redirect to projects page
        setTimeout(() => {
            window.location.href = '/projects';
        }, 1500);
    })
    .catch(error => {
        console.error('Error creating project from template:', error);
        showNotification('Error creating project from template', 'error');
    });
}

// Get template data based on type
function getTemplateData(templateType) {
    const templates = {
        'content_calendar': {
            title: 'Content Calendar Project',
            description: 'Plan and organize your content creation schedule',
            category: 'content',
            color: 'bg-pink-400',
            status: 'planning'
        },
        'study_plan': {
            title: 'Study Plan Project',
            description: 'Organize your study schedule and track progress',
            category: 'study',
            color: 'bg-blue-400',
            status: 'planning'
        },
        'project_board': {
            title: 'Project Board',
            description: 'Kanban-style project management board',
            category: 'personal',
            color: 'bg-green-400',
            status: 'planning'
        },
        'freelance_tracker': {
            title: 'Freelance Tracker',
            description: 'Track client projects and deadlines',
            category: 'freelance',
            color: 'bg-purple-400',
            status: 'planning'
        }
    };
    
    return templates[templateType] || templates['content_calendar'];
}

// Create custom template
function createTemplate() {
    document.getElementById('templateModal').classList.remove('hidden');
    document.getElementById('templateModal').classList.add('flex');
}

function closeTemplateModal() {
    document.getElementById('templateModal').classList.add('hidden');
    document.getElementById('templateModal').classList.remove('flex');
    document.getElementById('templateForm').reset();
}

// Template form submission
document.getElementById('templateForm').addEventListener('submit', function(e) {
    e.preventDefault();
    
    const templateData = {
        name: document.getElementById('templateName').value,
        description: document.getElementById('templateDescription').value,
        category: document.getElementById('templateCategory').value,
        templateData: JSON.stringify({
            structure: 'custom',
            sections: ['planning', 'execution', 'review']
        }),
        isPublic: false
    };
    
    fetch('/api/templates', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(templateData)
    })
    .then(response => response.json())
    .then(template => {
        showNotification('Template created successfully! 🎨', 'success');
        closeTemplateModal();
        setTimeout(() => location.reload(), 1000);
    })
    .catch(error => {
        console.error('Error creating template:', error);
        showNotification('Error creating template', 'error');
    });
});

// Drag and drop functionality
function initializeTemplateDragDrop() {
    const templateCards = document.querySelectorAll('.template-card');
    
    templateCards.forEach(card => {
        card.addEventListener('dragstart', function(e) {
            e.dataTransfer.setData('text/plain', this.getAttribute('data-category'));
            this.classList.add('dragging');
        });
        
        card.addEventListener('dragend', function() {
            this.classList.remove('dragging');
        });
    });
    
    // Make project creation area a drop zone
    const dropZone = document.createElement('div');
    dropZone.className = 'fixed bottom-4 right-4 w-64 h-32 bg-white border-2 border-dashed border-purple-300 rounded-xl flex items-center justify-center text-purple-600 font-medium opacity-0 transition-opacity';
    dropZone.innerHTML = '<i class="fas fa-plus mr-2"></i>Drop here to create project';
    document.body.appendChild(dropZone);
    
    document.addEventListener('dragover', function(e) {
        e.preventDefault();
        dropZone.style.opacity = '1';
    });
    
    document.addEventListener('dragleave', function(e) {
        if (!e.relatedTarget) {
            dropZone.style.opacity = '0';
        }
    });
    
    dropZone.addEventListener('drop', function(e) {
        e.preventDefault();
        const templateType = e.dataTransfer.getData('text/plain');
        useTemplate(templateType);
        dropZone.style.opacity = '0';
    });
}

// Notification function
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
    
    setTimeout(() => {
        if (notification.parentElement) {
            notification.remove();
        }
    }, 5000);
}

// Initialize page
document.addEventListener('DOMContentLoaded', function() {
    initializeTemplateDragDrop();
    
    // Set default active filter
    const allFilter = document.querySelector('.template-filter');
    if (allFilter) {
        allFilter.classList.add('active', 'bg-purple-100', 'text-purple-700');
    }
});