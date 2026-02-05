import { Link } from 'react-router-dom';
import { Sparkles, Github, Linkedin, Twitter, Heart } from 'lucide-react';
import './Footer.css';

const Footer = () => {
    const currentYear = new Date().getFullYear();

    return (
        <footer className="app-footer">
            <div className="footer-container">
                {/* Brand */}
                <div className="footer-brand">
                    <Link to="/" className="footer-logo">
                        <Sparkles size={20} />
                        <span>BetterMe</span>
                    </Link>
                    <p className="footer-tagline">
                        Build better habits. Become your best self.
                    </p>
                </div>

                {/* Links */}
                <div className="footer-links">
                    <div className="footer-column">
                        <h4>Product</h4>
                        <Link to="/dashboard">Dashboard</Link>
                        <Link to="/habits">Habits</Link>
                        <Link to="/goals">Goals</Link>
                        <Link to="/analytics">Analytics</Link>
                    </div>
                    <div className="footer-column">
                        <h4>Resources</h4>
                        <a href="#">Help Center</a>
                        <a href="#">Privacy Policy</a>
                        <a href="#">Terms of Service</a>
                    </div>
                </div>

                {/* Social */}
                <div className="footer-social">
                    <a href="https://github.com" target="_blank" rel="noopener noreferrer" aria-label="GitHub">
                        <Github size={18} />
                    </a>
                    <a href="https://linkedin.com" target="_blank" rel="noopener noreferrer" aria-label="LinkedIn">
                        <Linkedin size={18} />
                    </a>
                    <a href="https://twitter.com" target="_blank" rel="noopener noreferrer" aria-label="Twitter">
                        <Twitter size={18} />
                    </a>
                </div>
            </div>

            {/* Bottom bar */}
            <div className="footer-bottom">
                <p>
                    © {currentYear} BetterMe. Made with <Heart size={14} className="heart-icon" /> for self-improvement
                </p>
            </div>
        </footer>
    );
};

export default Footer;
