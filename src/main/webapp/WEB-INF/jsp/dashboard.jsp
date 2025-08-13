<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>CreativeFlow - Your Gen-Z Workspace</title>
    <link href="https://cdn.jsdelivr.net/npm/tailwindcss@2.2.19/dist/tailwind.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
</head>
<body class="bg-gradient-to-br from-purple-50 to-pink-50 min-h-screen">
    <!-- Navigation -->
    <nav class="bg-white shadow-lg border-b-4 border-gradient-to-r from-purple-400 to-pink-400">
        <div class="max-w-7xl mx-auto px-4">
            <div class="flex justify-between items-center py-4">
                <div class="flex items-center space-x-4">
                    <h1 class="text-2xl font-bold bg-gradient-to-r from-purple-600 to-pink-600 bg-clip-text text-transparent">
                        ✨ CreativeFlow
                    </h1>
                </div>
                <div class="flex items-center space-x-4">
                    <div class="flex items-center space-x-2">
                        <img src="https://via.placeholder.com/40" alt="Profile" class="w-10 h-10 rounded-full">
                        <span class="font-medium text-gray-700">${user.name}</span>
                    </div>
                </div>
            </div>
        </div>
    </nav>

    <div class="max-w-7xl mx-auto px-4 py-8">
        <!-- Welcome Section -->
        <div class="mb-8">
            <h2 class="text-3xl font-bold text-gray-800 mb-2">
                Hey <span class="text-purple-600">${user.name}</span>! 👋
            </h2>
            <p class="text-gray-600">Ready to create something amazing today?</p>
        </div>

        <!-- Quick Actions -->
        <div class="grid grid-cols-1 md:grid-cols-4 gap-6 mb-8">
            <button onclick="createProject('content')" class="bg-gradient-to-r from-pink-400 to-red-400 text-white p-6 rounded-2xl shadow-lg hover:shadow-xl transform hover:-translate-y-1 transition-all">
                <i class="fas fa-video text-2xl mb-3"></i>
                <h3 class="font-semibold">Content Project</h3>
                <p class="text-sm opacity-90">Videos, posts, stories</p>
            </button>
            
            <button onclick="createProject('study')" class="bg-gradient-to-r from-blue-400 to-indigo-400 text-white p-6 rounded-2xl shadow-lg hover:shadow-xl transform hover:-translate-y-1 transition-all">
                <i class="fas fa-book text-2xl mb-3"></i>
                <h3 class="font-semibold">Study Plan</h3>
                <p class="text-sm opacity-90">Courses, assignments</p>
            </button>
            
            <button onclick="createProject('freelance')" class="bg-gradient-to-r from-green-400 to-teal-400 text-white p-6 rounded-2xl shadow-lg hover:shadow-xl transform hover:-translate-y-1 transition-all">
                <i class="fas fa-briefcase text-2xl mb-3"></i>
                <h3 class="font-semibold">Freelance Work</h3>
                <p class="text-sm opacity-90">Client projects</p>
            </button>
            
            <button onclick="createProject('personal')" class="bg-gradient-to-r from-purple-400 to-pink-400 text-white p-6 rounded-2xl shadow-lg hover:shadow-xl transform hover:-translate-y-1 transition-all">
                <i class="fas fa-heart text-2xl mb-3"></i>
                <h3 class="font-semibold">Personal</h3>
                <p class="text-sm opacity-90">Goals, habits</p>
            </button>
        </div>

        <!-- Recent Projects -->
        <div class="bg-white rounded-2xl shadow-lg p-6">
            <div class="flex justify-between items-center mb-6">
                <h3 class="text-xl font-semibold text-gray-800">Recent Projects</h3>
                <a href="/projects" class="text-purple-600 hover:text-purple-700 font-medium">View All</a>
            </div>
            
            <c:choose>
                <c:when test="${not empty recentProjects}">
                    <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
                        <c:forEach var="project" items="${recentProjects}">
                            <div class="border border-gray-200 rounded-xl p-4 hover:shadow-md transition-shadow cursor-pointer" 
                                 onclick="window.location.href='/projects/${project.id}'">
                                <div class="flex items-center justify-between mb-3">
                                    <div class="w-4 h-4 rounded-full ${project.color != null ? project.color : 'bg-purple-400'}"></div>
                                    <span class="text-xs bg-gray-100 px-2 py-1 rounded-full text-gray-600 capitalize">${project.status}</span>
                                </div>
                                <h4 class="font-semibold text-gray-800 mb-2">${project.title}</h4>
                                <p class="text-gray-600 text-sm mb-3">${project.description}</p>
                                <div class="flex items-center justify-between text-xs text-gray-500">
                                    <span class="capitalize">${project.category}</span>
                                    <span><fmt:formatDate value="${project.createdAt}" pattern="MMM dd"/></span>
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="text-center py-12">
                        <i class="fas fa-rocket text-4xl text-gray-300 mb-4"></i>
                        <p class="text-gray-500 mb-4">No projects yet! Start creating something amazing.</p>
                        <button onclick="createProject('content')" class="bg-purple-600 text-white px-6 py-2 rounded-lg hover:bg-purple-700 transition-colors">
                            Create Your First Project
                        </button>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </div>

    <!-- Project Creation Modal -->
    <div id="projectModal" class="fixed inset-0 bg-black bg-opacity-50 hidden items-center justify-center z-50">
        <div class="bg-white rounded-2xl p-8 max-w-md w-full mx-4">
            <h3 class="text-2xl font-bold text-gray-800 mb-6">Create New Project</h3>
            <form id="projectForm">
                <div class="mb-4">
                    <label class="block text-gray-700 font-medium mb-2">Project Title</label>
                    <input type="text" id="projectTitle" class="w-full p-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-purple-500 focus:border-transparent">
                </div>
                <div class="mb-4">
                    <label class="block text-gray-700 font-medium mb-2">Description</label>
                    <textarea id="projectDescription" rows="3" class="w-full p-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-purple-500 focus:border-transparent"></textarea>
                </div>
                <div class="mb-6">
                    <label class="block text-gray-700 font-medium mb-2">Category</label>
                    <select id="projectCategory" class="w-full p-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-purple-500 focus:border-transparent">
                        <option value="content">Content</option>
                        <option value="study">Study</option>
                        <option value="freelance">Freelance</option>
                        <option value="personal">Personal</option>
                    </select>
                </div>
                <div class="flex space-x-4">
                    <button type="button" onclick="closeModal()" class="flex-1 bg-gray-300 text-gray-700 py-3 rounded-lg hover:bg-gray-400 transition-colors">
                        Cancel
                    </button>
                    <button type="submit" class="flex-1 bg-purple-600 text-white py-3 rounded-lg hover:bg-purple-700 transition-colors">
                        Create Project
                    </button>
                </div>
            </form>
        </div>
    </div>

    <script src="/js/dashboard.js"></script>
</body>
</html>