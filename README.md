# CreativeFlow - Gen-Z Workspace Platform

CreativeFlow is an intuitive, visually-rich, AI-supported workspace designed specifically for Gen-Z creators, students, and freelancers who work in hybrid mode.

## 🌟 Features

### Core Functionality
- **Project Management**: Organize content, study, freelance, and personal projects
- **AI-Powered Suggestions**: Smart recommendations for deadlines, content ideas, and time allocation
- **Visual Organization**: Color-coded projects with modern, Gen-Z friendly interface
- **Task Management**: Break down projects into manageable tasks with priority levels
- **Note-Taking**: Support for text notes with planned voice-to-text integration

### AI Intelligence
- **Smart Deadlines**: AI suggests realistic project timelines based on type and complexity
- **Content Ideas**: Generate creative suggestions based on project category
- **Time Allocation**: Intelligent breakdown of time needed for different project phases
- **Collaboration Suggestions**: Recommend team members based on project requirements
- **Smart Reminders**: Contextual notifications to keep users on track

### User Experience
- **Drag-and-Drop Templates**: Pre-built project templates for quick setup
- **Responsive Design**: Works seamlessly on desktop and mobile devices
- **Modern UI**: Gradient backgrounds, smooth animations, and intuitive navigation
- **Real-time Updates**: Live project status and progress tracking

## 🚀 Technology Stack

- **Backend**: Spring Boot 3.2.0, Java 17
- **Database**: MySQL with JPA/Hibernate
- **Frontend**: Thymeleaf, Tailwind CSS, Vanilla JavaScript
- **Features**: WebSocket support, File upload, RESTful APIs

## 📋 Prerequisites

- Java 17 or higher
- MySQL 8.0 or higher
- Gradle 7.0 or higher

## 🛠️ Setup Instructions

### 1. Database Setup
```sql
CREATE DATABASE creativeflow_db;
CREATE USER 'creativeflow'@'localhost' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON creativeflow_db.* TO 'creativeflow'@'localhost';
FLUSH PRIVILEGES;
```

### 2. Application Configuration
Update `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/creativeflow_db
spring.datasource.username=your_username
spring.datasource.password=your_password
```

### 3. Run the Application
```bash
./gradlew bootRun
```

The application will be available at `http://localhost:8080`

## 🎯 Target Audience

**Gen-Z Creators, Students, and Freelancers** who need:
- Intuitive project management without feature bloat
- Visual and inspiring workspace design
- AI assistance for planning and organization
- Seamless collaboration tools
- Mobile-friendly interface

## 🔮 Planned Features

### Phase 2
- **Voice-to-Text Notes**: Record ideas and convert to text automatically
- **Content Moodboards**: Visual inspiration boards with drag-and-drop images
- **Real-time Collaboration**: Live editing and commenting
- **Advanced Templates**: Community-shared project templates

### Phase 3
- **Mobile App**: Native iOS and Android applications
- **Integration Hub**: Connect with popular tools (Notion, Trello, Google Drive)
- **Analytics Dashboard**: Track productivity and project success metrics
- **AI Content Generation**: Generate content ideas and outlines

## 🏗️ Project Structure

```
src/
├── main/
│   ├── java/org/example/
│   │   ├── entity/          # Database entities
│   │   ├── repository/      # Data access layer
│   │   ├── service/         # Business logic
│   │   ├── controller/      # Web controllers
│   │   └── Main.java        # Application entry point
│   └── resources/
│       ├── static/          # CSS, JS, images
│       ├── templates/       # Thymeleaf templates
│       └── application.properties
```

## 🎨 Design Philosophy

CreativeFlow embraces Gen-Z aesthetics and workflow preferences:
- **Visual First**: Color-coded organization and gradient designs
- **Mobile Native**: Touch-friendly interface with responsive design
- **AI Enhanced**: Intelligent suggestions without overwhelming complexity
- **Community Driven**: Shareable templates and collaborative features

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🙏 Acknowledgments

- Tailwind CSS for the beautiful styling framework
- Font Awesome for the comprehensive icon library
- Spring Boot team for the excellent framework
- Gen-Z creators who inspired this project

---

**CreativeFlow** - Where creativity meets productivity ✨