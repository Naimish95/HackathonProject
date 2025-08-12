// Project detail page functionality

let isRecording = false;
let mediaRecorder;
let audioChunks = [];

// Task management
function addTask() {
    const taskTitle = prompt('Enter task title:');
    if (!taskTitle) return;
    
    const taskData = {
        title: taskTitle,
        description: '',
        priority: 'medium',
        status: 'todo',
        project: { id: getProjectId() }
    };
    
    fetch('/api/tasks', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(taskData)
    })
    .then(response => response.json())
    .then(task => {
        addTaskToList(task);
        showNotification('Task added successfully! ✅', 'success');
    })
    .catch(error => {
        console.error('Error adding task:', error);
        showNotification('Error adding task', 'error');
    });
}

function addTaskToList(task) {
    const tasksList = document.getElementById('tasksList');
    const taskElement = document.createElement('div');
    taskElement.className = 'flex items-center space-x-4 p-4 border border-gray-200 rounded-lg';
    taskElement.innerHTML = `
        <input type="checkbox" class="w-5 h-5 text-purple-600 rounded" 
               ${task.status === 'completed' ? 'checked' : ''} 
               onchange="toggleTaskStatus(${task.id}, this.checked)">
        <div class="flex-1">
            <h4 class="font-medium text-gray-800">${task.title}</h4>
            <p class="text-sm text-gray-500">${task.description || 'No description'}</p>
        </div>
        <span class="px-2 py-1 ${getPriorityColor(task.priority)} text-xs rounded-full">${task.priority}</span>
        <button onclick="deleteTask(${task.id})" class="text-gray-400 hover:text-red-600">
            <i class="fas fa-trash"></i>
        </button>
    `;
    tasksList.appendChild(taskElement);
}

function toggleTaskStatus(taskId, completed) {
    const status = completed ? 'completed' : 'todo';
    
    fetch(`/api/tasks/${taskId}/status?status=${status}`, {
        method: 'PUT'
    })
    .then(response => response.json())
    .then(task => {
        showNotification(`Task ${completed ? 'completed' : 'reopened'}! 🎯`, 'success');
        updateProgressChart();
    })
    .catch(error => {
        console.error('Error updating task status:', error);
        showNotification('Error updating task', 'error');
    });
}

function deleteTask(taskId) {
    if (!confirm('Are you sure you want to delete this task?')) return;
    
    fetch(`/api/tasks/${taskId}`, {
        method: 'DELETE'
    })
    .then(() => {
        document.querySelector(`[onclick="deleteTask(${taskId})"]`).closest('.flex').remove();
        showNotification('Task deleted! 🗑️', 'success');
        updateProgressChart();
    })
    .catch(error => {
        console.error('Error deleting task:', error);
        showNotification('Error deleting task', 'error');
    });
}

// Notes management
function addTextNote() {
    const noteTitle = prompt('Enter note title:');
    if (!noteTitle) return;
    
    const noteContent = prompt('Enter note content:');
    if (!noteContent) return;
    
    const noteData = {
        title: noteTitle,
        content: noteContent,
        type: 'text',
        project: { id: getProjectId() }
    };
    
    fetch('/api/notes', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(noteData)
    })
    .then(response => response.json())
    .then(note => {
        addNoteToList(note);
        showNotification('Note added successfully! 📝', 'success');
    })
    .catch(error => {
        console.error('Error adding note:', error);
        showNotification('Error adding note', 'error');
    });
}

function addNoteToList(note) {
    const notesList = document.getElementById('notesList');
    const noteElement = document.createElement('div');
    noteElement.className = 'border border-gray-200 rounded-lg p-3';
    noteElement.innerHTML = `
        <h4 class="font-medium text-gray-800 text-sm mb-1">${note.title}</h4>
        <p class="text-xs text-gray-500">${note.content.substring(0, 50)}${note.content.length > 50 ? '...' : ''}</p>
        <div class="flex items-center justify-between mt-2">
            <span class="text-xs text-gray-400">Just now</span>
            <span class="text-xs ${getTypeColor(note.type)} px-2 py-1 rounded">${note.type}</span>
        </div>
    `;
    notesList.appendChild(noteElement);
}

// Voice recording functionality
function startVoiceNote() {
    document.getElementById('voiceRecorder').classList.remove('hidden');
}

function cancelVoiceNote() {
    document.getElementById('voiceRecorder').classList.add('hidden');
    stopRecording();
}

function saveVoiceNote() {
    if (audioChunks.length === 0) {
        showNotification('No recording found', 'error');
        return;
    }
    
    const audioBlob = new Blob(audioChunks, { type: 'audio/wav' });
    const formData = new FormData();
    formData.append('file', audioBlob, 'voice-note.wav');
    formData.append('projectId', getProjectId());
    formData.append('title', 'Voice Note ' + new Date().toLocaleTimeString());
    
    fetch('/api/notes/voice', {
        method: 'POST',
        body: formData
    })
    .then(response => response.json())
    .then(note => {
        addNoteToList(note);
        showNotification('Voice note saved! 🎤', 'success');
        cancelVoiceNote();
        audioChunks = [];
    })
    .catch(error => {
        console.error('Error saving voice note:', error);
        showNotification('Error saving voice note', 'error');
    });
}

// Initialize voice recording
document.getElementById('recordButton').addEventListener('click', function() {
    if (!isRecording) {
        startRecording();
    } else {
        stopRecording();
    }
});

function startRecording() {
    navigator.mediaDevices.getUserMedia({ audio: true })
        .then(stream => {
            mediaRecorder = new MediaRecorder(stream);
            mediaRecorder.start();
            isRecording = true;
            
            document.getElementById('recordButton').innerHTML = '<i class="fas fa-stop text-xl"></i>';
            document.getElementById('recordButton').classList.remove('bg-red-500', 'hover:bg-red-600');
            document.getElementById('recordButton').classList.add('bg-gray-500', 'hover:bg-gray-600', 'recording');
            
            mediaRecorder.addEventListener('dataavailable', event => {
                audioChunks.push(event.data);
            });
            
            mediaRecorder.addEventListener('stop', () => {
                stream.getTracks().forEach(track => track.stop());
            });
        })
        .catch(error => {
            console.error('Error accessing microphone:', error);
            showNotification('Error accessing microphone', 'error');
        });
}

function stopRecording() {
    if (mediaRecorder && isRecording) {
        mediaRecorder.stop();
        isRecording = false;
        
        document.getElementById('recordButton').innerHTML = '<i class="fas fa-microphone text-xl"></i>';
        document.getElementById('recordButton').classList.remove('bg-gray-500', 'hover:bg-gray-600', 'recording');
        document.getElementById('recordButton').classList.add('bg-red-500', 'hover:bg-red-600');
    }
}

// Utility functions
function getProjectId() {
    // In a real app, this would come from URL params or page data
    return 1;
}

function getPriorityColor(priority) {
    const colors = {
        'low': 'bg-green-100 text-green-800',
        'medium': 'bg-yellow-100 text-yellow-800',
        'high': 'bg-orange-100 text-orange-800',
        'urgent': 'bg-red-100 text-red-800'
    };
    return colors[priority] || colors['medium'];
}

function getTypeColor(type) {
    const colors = {
        'text': 'bg-blue-100 text-blue-800',
        'voice': 'bg-red-100 text-red-800',
        'image': 'bg-green-100 text-green-800',
        'moodboard': 'bg-purple-100 text-purple-800'
    };
    return colors[type] || colors['text'];
}

function updateProgressChart() {
    // This would update the progress chart based on task completion
    console.log('Updating progress chart...');
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
    
    setTimeout(() => {
        if (notification.parentElement) {
            notification.remove();
        }
    }, 5000);
}

// Initialize page
document.addEventListener('DOMContentLoaded', function() {
    // Load project data, tasks, and notes
    loadProjectData();
});

function loadProjectData() {
    // This would load actual project data from the server
    console.log('Loading project data...');
}