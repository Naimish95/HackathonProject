// Projects page functionality

// Filter projects by category
function filterProjects(category) {
    const projectCards = document.querySelectorAll('.project-card');
    const filterTabs = document.querySelectorAll('.filter-tab');
    const emptyState = document.getElementById('emptyState');
    
    // Update active tab
    filterTabs.forEach(tab => tab.classList.remove('active', 'bg-purple-100', 'text-purple-700'));
    event.target.classList.add('active', 'bg-purple-100', 'text-purple-700');
    
    let visibleCount = 0;
    
    projectCards.forEach(card => {
        const cardCategory = card.getAttribute('data-category');
        
        if (category === 'all' || cardCategory === category) {
            card.style.display = 'block';
            visibleCount++;
        } else {
            card.style.display = 'none';
        }
    });
    
    // Show/hide empty state
    if (visibleCount === 0) {
        emptyState.classList.remove('hidden');
        document.getElementById('projectsGrid').classList.add('hidden');
    } else {
        emptyState.classList.add('hidden');
        document.getElementById('projectsGrid').classList.remove('hidden');
    }
}

// Toggle project menu
function toggleProjectMenu(button) {
    const menu = button.nextElementSibling;
    const allMenus = document.querySelectorAll('.project-menu');
    
    // Close all other menus
    allMenus.forEach(m => {
        if (m !== menu) {
            m.classList.add('hidden');
        }
    });
    
    // Toggle current menu
    menu.classList.toggle('hidden');
}

// Close menus when clicking outside
document.addEventListener('click', function(e) {
    if (!e.target.closest('.relative')) {
        document.querySelectorAll('.project-menu').forEach(menu => {
            menu.classList.add('hidden');
        });
    }
});

// Project actions
function editProject(projectId) {
    fetch(`/api/projects/${projectId}`)
        .then(response => response.json())
        .then(project => {
            showEditProjectModal(project);
        })
        .catch(error => {
            console.error('Error fetching project:', error);
            showNotification('Error loading project details', 'error');
        });
}

function showEditProjectModal(project) {
    const modal = document.createElement('div');
    modal.className = 'fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50';
    modal.innerHTML = `
        <div class="bg-white rounded-2xl p-8 max-w-md w-full mx-4">
            <h3 class="text-2xl font-bold text-gray-800 mb-6">Edit Project</h3>
            <form id="editProjectForm">
                <div class="mb-4">
                    <label class="block text-gray-700 font-medium mb-2">Project Title</label>
                    <div class="relative">
                        <input type="text" id="editTitle" value="${project.title}" class="w-full p-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-purple-500">
                        <button type="button" onclick="startVoiceToText('editTitle')" class="absolute right-2 top-2 text-gray-400 hover:text-purple-600">
                            <i class="fas fa-microphone"></i>
                        </button>
                    </div>
                </div>
                <div class="mb-4">
                    <label class="block text-gray-700 font-medium mb-2">Description</label>
                    <div class="relative">
                        <textarea id="editDescription" rows="3" class="w-full p-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-purple-500">${project.description || ''}</textarea>
                        <button type="button" onclick="startVoiceToText('editDescription')" class="absolute right-2 top-2 text-gray-400 hover:text-purple-600">
                            <i class="fas fa-microphone"></i>
                        </button>
                    </div>
                </div>
                <div class="mb-6">
                    <label class="block text-gray-700 font-medium mb-2">Category</label>
                    <select id="editCategory" class="w-full p-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-purple-500">
                        <option value="content" ${project.category === 'content' ? 'selected' : ''}>Content</option>
                        <option value="study" ${project.category === 'study' ? 'selected' : ''}>Study</option>
                        <option value="freelance" ${project.category === 'freelance' ? 'selected' : ''}>Freelance</option>
                        <option value="personal" ${project.category === 'personal' ? 'selected' : ''}>Personal</option>
                    </select>
                </div>
                <div class="flex space-x-4">
                    <button type="button" onclick="closeEditModal()" class="flex-1 bg-gray-300 text-gray-700 py-3 rounded-lg hover:bg-gray-400 transition-colors">
                        Cancel
                    </button>
                    <button type="submit" class="flex-1 bg-purple-600 text-white py-3 rounded-lg hover:bg-purple-700 transition-colors">
                        Save Changes
                    </button>
                </div>
            </form>
        </div>
    `;
    
    document.body.appendChild(modal);
    
    document.getElementById('editProjectForm').addEventListener('submit', function(e) {
        e.preventDefault();
        updateProject(project.id);
    });
}

function updateProject(projectId) {
    const projectData = {
        title: document.getElementById('editTitle').value,
        description: document.getElementById('editDescription').value,
        category: document.getElementById('editCategory').value
    };
    
    fetch(`/api/projects/${projectId}`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(projectData)
    })
    .then(response => {
        if (response.ok) {
            showNotification('Project updated successfully! ✅', 'success');
            closeEditModal();
            setTimeout(() => location.reload(), 1000);
        } else {
            showNotification('Error updating project', 'error');
        }
    })
    .catch(error => {
        console.error('Error updating project:', error);
        showNotification('Network error updating project', 'error');
    });
}

function closeEditModal() {
    const modal = document.querySelector('.fixed.inset-0');
    if (modal) {
        modal.remove();
    }
}

function duplicateProject(projectId) {
    // Implementation for duplicating project
    console.log('Duplicate project:', projectId);
}

function deleteProject(projectId) {
    if (confirm('Are you sure you want to delete this project?')) {
        // Implementation for deleting project
        console.log('Delete project:', projectId);
    }
}

// Search functionality
function initializeSearch() {
    const searchInput = document.createElement('input');
    searchInput.type = 'text';
    searchInput.placeholder = 'Search projects...';
    searchInput.className = 'w-full max-w-md px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-purple-500 focus:border-transparent';
    
    searchInput.addEventListener('input', function(e) {
        const searchTerm = e.target.value.toLowerCase();
        const projectCards = document.querySelectorAll('.project-card');
        
        projectCards.forEach(card => {
            const title = card.querySelector('h3').textContent.toLowerCase();
            const description = card.querySelector('p').textContent.toLowerCase();
            
            if (title.includes(searchTerm) || description.includes(searchTerm)) {
                card.style.display = 'block';
            } else {
                card.style.display = 'none';
            }
        });
    });
    
    // Add search input to header
    const header = document.querySelector('.flex.justify-between.items-center.mb-8');
    const searchContainer = document.createElement('div');
    searchContainer.className = 'flex items-center space-x-4';
    searchContainer.appendChild(searchInput);
    searchContainer.appendChild(header.querySelector('button'));
    
    header.replaceChild(searchContainer, header.querySelector('button'));
}

// Drag and drop for project reordering
function initializeProjectDragDrop() {
    const projectCards = document.querySelectorAll('.project-card');
    
    projectCards.forEach(card => {
        card.draggable = true;
        
        card.addEventListener('dragstart', function(e) {
            e.dataTransfer.setData('text/plain', '');
            this.classList.add('dragging');
        });
        
        card.addEventListener('dragend', function() {
            this.classList.remove('dragging');
        });
        
        card.addEventListener('dragover', function(e) {
            e.preventDefault();
            this.classList.add('drag-over');
        });
        
        card.addEventListener('dragleave', function() {
            this.classList.remove('drag-over');
        });
        
        card.addEventListener('drop', function(e) {
            e.preventDefault();
            this.classList.remove('drag-over');
            
            const draggingCard = document.querySelector('.dragging');
            if (draggingCard && draggingCard !== this) {
                // Reorder projects
                const grid = document.getElementById('projectsGrid');
                const afterElement = getDragAfterElement(grid, e.clientY);
                
                if (afterElement == null) {
                    grid.appendChild(draggingCard);
                } else {
                    grid.insertBefore(draggingCard, afterElement);
                }
            }
        });
    });
}

function getDragAfterElement(container, y) {
    const draggableElements = [...container.querySelectorAll('.project-card:not(.dragging)')];
    
    return draggableElements.reduce((closest, child) => {
        const box = child.getBoundingClientRect();
        const offset = y - box.top - box.height / 2;
        
        if (offset < 0 && offset > closest.offset) {
            return { offset: offset, element: child };
        } else {
            return closest;
        }
    }, { offset: Number.NEGATIVE_INFINITY }).element;
}

// Initialize page
document.addEventListener('DOMContentLoaded', function() {
    initializeSearch();
    initializeProjectDragDrop();
    
    // Set default active filter
    const allTab = document.querySelector('.filter-tab');
    if (allTab) {
        allTab.classList.add('active', 'bg-purple-100', 'text-purple-700');
    }
});