<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Project Details - CreativeFlow</title>
    <link href="https://cdn.jsdelivr.net/npm/tailwindcss@2.2.19/dist/tailwind.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
</head>
<body class="bg-gradient-to-br from-purple-50 to-pink-50 min-h-screen">
    <!-- Navigation -->
    <nav class="bg-white shadow-lg border-b-4 border-gradient-to-r from-purple-400 to-pink-400">
        <div class="max-w-7xl mx-auto px-4">
            <div class="flex justify-between items-center py-4">
                <div class="flex items-center space-x-4">
                    <a href="/" class="text-2xl font-bold bg-gradient-to-r from-purple-600 to-pink-600 bg-clip-text text-transparent">
                        ✨ CreativeFlow
                    </a>
                </div>
                <div class="flex items-center space-x-6">
                    <a href="/" class="text-gray-600 hover:text-purple-600 transition-colors">Dashboard</a>
                    <a href="/projects" class="text-purple-600 font-medium">Projects</a>
                </div>
            </div>
        </div>
    </nav>

    <div class="max-w-7xl mx-auto px-4 py-8">
        <!-- Project Header -->
        <div class="bg-white rounded-2xl shadow-lg p-8 mb-8">
            <div class="flex items-center justify-between mb-6">
                <div class="flex items-center space-x-4">
                    <div class="w-6 h-6 rounded-full ${project.color != null ? project.color : 'bg-purple-400'}"></div>
                    <h1 class="text-3xl font-bold text-gray-800">${project.title}</h1>
                </div>
                <div class="flex items-center space-x-4">
                    <span class="px-4 py-2 rounded-full text-sm font-medium capitalize 
                        <c:choose>
                            <c:when test='${project.status == "active"}'>bg-blue-100 text-blue-800</c:when>
                            <c:when test='${project.status == "completed"}'>bg-green-100 text-green-800</c:when>
                            <c:when test='${project.status == "paused"}'>bg-yellow-100 text-yellow-800</c:when>
                            <c:otherwise>bg-gray-100 text-gray-800</c:otherwise>
                        </c:choose>">
                        ${project.status}
                    </span>
                    <button class="bg-purple-600 text-white px-6 py-2 rounded-lg hover:bg-purple-700 transition-colors">
                        <i class="fas fa-edit mr-2"></i>Edit
                    </button>
                </div>
            </div>
            
            <p class="text-gray-600 mb-6">${project.description}</p>
            
            <div class="grid grid-cols-1 md:grid-cols-4 gap-6">
                <div class="text-center">
                    <div class="text-2xl font-bold text-purple-600">
                        <c:choose>
                            <c:when test="${totalTasks > 0}">
                                ${(completedTasks * 100) / totalTasks}%
                            </c:when>
                            <c:otherwise>0%</c:otherwise>
                        </c:choose>
                    </div>
                    <div class="text-sm text-gray-500">Progress</div>
                </div>
                <div class="text-center">
                    <div class="text-2xl font-bold text-blue-600">${totalTasks}</div>
                    <div class="text-sm text-gray-500">Tasks</div>
                </div>
                <div class="text-center">
                    <div class="text-2xl font-bold text-green-600">${totalNotes}</div>
                    <div class="text-sm text-gray-500">Notes</div>
                </div>
                <div class="text-center">
                    <div class="text-2xl font-bold text-orange-600">
                        <c:choose>
                            <c:when test="${project.deadline != null}">
                                <fmt:formatDate value="${project.deadline}" pattern="MMM dd"/>
                            </c:when>
                            <c:otherwise>No deadline</c:otherwise>
                        </c:choose>
                    </div>
                    <div class="text-sm text-gray-500">Deadline</div>
                </div>
            </div>
        </div>

        <div class="grid grid-cols-1 lg:grid-cols-3 gap-8">
            <!-- Tasks Section -->
            <div class="lg:col-span-2">
                <div class="bg-white rounded-2xl shadow-lg p-6 mb-8">
                    <div class="flex items-center justify-between mb-6">
                        <h2 class="text-xl font-semibold text-gray-800">Tasks</h2>
                        <button onclick="addTask()" class="bg-purple-100 text-purple-700 px-4 py-2 rounded-lg hover:bg-purple-200 transition-colors">
                            <i class="fas fa-plus mr-2"></i>Add Task
                        </button>
                    </div>
                    
                    <div id="tasksList" class="space-y-4">
                        <c:choose>
                            <c:when test="${not empty project.tasks}">
                                <c:forEach var="task" items="${project.tasks}">
                                    <div class="flex items-center space-x-4 p-4 border border-gray-200 rounded-lg">
                                        <input type="checkbox" class="w-5 h-5 text-purple-600 rounded" 
                                               ${task.status == 'completed' ? 'checked' : ''} 
                                               onchange="toggleTaskStatus(${task.id}, this.checked)">
                                        <div class="flex-1 cursor-pointer" onclick="editTask(${task.id})">
                                            <h4 class="font-medium text-gray-800">${task.title}</h4>
                                            <p class="text-sm text-gray-500">${task.description != null ? task.description : 'No description'}</p>
                                            <c:if test="${task.deadline != null}">
                                                <div class="text-xs text-gray-400 mt-1">
                                                    <span>Deadline: </span>
                                                    <span><fmt:formatDate value="${task.deadline}" pattern="MMM dd, yyyy"/></span>
                                                </div>
                                            </c:if>
                                        </div>
                                        <span class="px-2 py-1 text-xs rounded-full capitalize 
                                            <c:choose>
                                                <c:when test='${task.priority == "low"}'>bg-green-100 text-green-800</c:when>
                                                <c:when test='${task.priority == "high"}'>bg-orange-100 text-orange-800</c:when>
                                                <c:when test='${task.priority == "urgent"}'>bg-red-100 text-red-800</c:when>
                                                <c:otherwise>bg-yellow-100 text-yellow-800</c:otherwise>
                                            </c:choose>">
                                            ${task.priority}
                                        </span>
                                        <button onclick="editTask(${task.id})" class="text-gray-400 hover:text-blue-600">
                                            <i class="fas fa-edit"></i>
                                        </button>
                                        <button onclick="deleteTask(${task.id})" class="text-gray-400 hover:text-red-600">
                                            <i class="fas fa-trash"></i>
                                        </button>
                                    </div>
                                </c:forEach>
                            </c:when>
                            <c:otherwise>
                                <div class="text-center py-8">
                                    <i class="fas fa-tasks text-4xl text-gray-300 mb-4"></i>
                                    <p class="text-gray-500 mb-4">No tasks yet! Add your first task to get started.</p>
                                    <button onclick="addTask()" class="bg-purple-600 text-white px-6 py-2 rounded-lg hover:bg-purple-700 transition-colors">
                                        <i class="fas fa-plus mr-2"></i>Add First Task
                                    </button>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </div>

            <!-- Sidebar -->
            <div class="space-y-8">
                <!-- Notes Section -->
                <div class="bg-white rounded-2xl shadow-lg p-6">
                    <div class="flex items-center justify-between mb-6">
                        <h3 class="text-lg font-semibold text-gray-800">Notes</h3>
                        <div class="flex space-x-2">
                            <button onclick="addTextNote()" class="bg-blue-100 text-blue-700 p-2 rounded-lg hover:bg-blue-200 transition-colors">
                                <i class="fas fa-sticky-note"></i>
                            </button>
                        </div>
                    </div>
                    
                    <div id="notesList" class="space-y-3">
                        <c:choose>
                            <c:when test="${not empty project.notes}">
                                <c:forEach var="note" items="${project.notes}">
                                    <div class="border border-gray-200 rounded-lg p-3">
                                        <h4 class="font-medium text-gray-800 text-sm mb-1">${note.title}</h4>
                                        <p class="text-xs text-gray-500">
                                            <c:choose>
                                                <c:when test="${note.content.length() > 50}">
                                                    ${note.content.substring(0, 50)}...
                                                </c:when>
                                                <c:otherwise>
                                                    ${note.content}
                                                </c:otherwise>
                                            </c:choose>
                                        </p>
                                        <div class="flex items-center justify-between mt-2">
                                            <span class="text-xs text-gray-400">
                                                <fmt:formatDate value="${note.createdAt}" pattern="MMM dd, HH:mm"/>
                                            </span>
                                            <span class="text-xs px-2 py-1 rounded capitalize 
                                                <c:choose>
                                                    <c:when test='${note.type == "text"}'>bg-blue-100 text-blue-800</c:when>
                                                    <c:when test='${note.type == "voice"}'>bg-red-100 text-red-800</c:when>
                                                    <c:when test='${note.type == "image"}'>bg-green-100 text-green-800</c:when>
                                                    <c:otherwise>bg-purple-100 text-purple-800</c:otherwise>
                                                </c:choose>">
                                                ${note.type}
                                            </span>
                                        </div>
                                    </div>
                                </c:forEach>
                            </c:when>
                            <c:otherwise>
                                <div class="text-center py-8">
                                    <i class="fas fa-sticky-note text-4xl text-gray-300 mb-4"></i>
                                    <p class="text-gray-500 mb-4">No notes yet! Add your first note.</p>
                                    <button onclick="addTextNote()" class="bg-blue-600 text-white px-6 py-2 rounded-lg hover:bg-blue-700 transition-colors">
                                        <i class="fas fa-plus mr-2"></i>Add First Note
                                    </button>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>

                <!-- Progress Chart -->
                <div class="bg-white rounded-2xl shadow-lg p-6">
                    <h3 class="text-lg font-semibold text-gray-800 mb-4">Progress Overview</h3>
                    <c:choose>
                        <c:when test="${totalTasks > 0}">
                            <div class="space-y-4">
                                <div>
                                    <div class="flex justify-between text-sm text-gray-600 mb-1">
                                        <span>Completed</span>
                                        <span>${completedTasks}/${totalTasks} tasks</span>
                                    </div>
                                    <div class="w-full bg-gray-200 rounded-full h-2">
                                        <div class="bg-green-500 h-2 rounded-full" style="width: ${totalTasks > 0 ? (completedTasks * 100 / totalTasks) : 0}%"></div>
                                    </div>
                                </div>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="text-center py-8">
                                <i class="fas fa-chart-bar text-4xl text-gray-300 mb-4"></i>
                                <p class="text-gray-500">Add tasks to see progress</p>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>
    </div>

    <script src="/js/project-detail.js"></script>
</body>
</html>